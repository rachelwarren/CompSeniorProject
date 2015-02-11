
/**
 * Utility methods for preprocessing and handling weka Instances objects
 * None of the methods in this class which take and return an instances object are 
 * mutator methods. All return a deep copy. 
 * 
 * @author  Rachel Warren 
 * @version  12/14/2013
 */

//Import statements 
import weka.core.stemmers.*;
import weka.core.FastVector; 
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*; 
import java.util.*; 
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.attribute.*; 
import weka.filters.Filter; 
import weka.classifiers.bayes.*; 
import weka.classifiers.Classifier;
import weka.core.tokenizers.*; 
import weka.classifiers.Evaluation;
import java.util.Random; 

public class InstanceUtils
{
    public static final int TITLE_IND = 3; //the original index of the title attribute 
    public static final int BODY_IND =  5;
    /**
     * This is a string of the indecies (begining at 1) of the attributes comming from the 
     * labeled data class which should be kept for the "is relevent model " 
     */
    public static final String INDEX_A = "1,2,3,4,5,6,9";
    /**
     * This is the a string fo the inedcies (begining at one for the "fact_checking 
     * model. 
     */
    public static final String INDEX_B = "1,2,3,4,5,6,8"; 
    /**
     * Indices (starting at one) of the lexisnumber, body text, and class variable. 
     */
    public static final String INDEX_ANALYSIS = "1,6,7"; 
    
    ///the original index of the body attribute 
    /**
     * Constructor for objects of class InstanceUtils
     */
    public InstanceUtils()
    {
      
    }
    
  
    /**
     * Builds a defualt string to word vector, it does not apply it, so that it can be adjusted 
     *Defualt string to word: 
     *      -has no stemmer 
     *      -uses word counts 
     *      - uses defualt tokenizer 
     *      - no stopwords 
     * @param  ai attribute indices 
     * @return     stwv-the string to word vector 
     */
    public static StringToWordVector buildSTWV() throws Exception 
    {
        StringToWordVector stwv = new StringToWordVector();
        NGramTokenizer tokenizer = new NGramTokenizer(); 
        tokenizer.setNGramMinSize(1); 
        tokenizer.setNGramMaxSize(1); 
        tokenizer.setDelimiters("\\W");
        stwv.setOptions(weka.core.Utils.splitOptions(" -P \"_\" -stemmer weka.core.stemmers.NullStemmer -W 2000 -tokenizer \"weka.core.tokenizers.WordTokenizer\"" ));
        //stwv.setAttributeIndicesArray(ai); 
        stwv.setOutputWordCounts(true); 
        stwv.setTokenizer(tokenizer); ; 
        stwv.setUseStoplist(false); //use the defualt weka stoplist 
        stwv.setLowerCaseTokens(true); 
        stwv.setLowerCaseTokens(true);
        //stwv.setInputFormat(i);
        return stwv;
    }

    /**
     * 
     * Applies a string to word vector to the current data 
     * Reorders the attributes so that class attribute is at the end. 
     *
     * @param  ai  the indices  of the body test to apply the vector to 
     * @param stwv
     * @param data 
     * @return  data' 
     */
    public static Instances applySTWV(int[] ai, StringToWordVector stwv, Instances data ) throws Exception
    {
        
        
        Instances data2 = new Instances(data); 
        stwv.setAttributeIndicesArray(ai); 
        stwv.setInputFormat(data2);
        data2 = Filter.useFilter(data2, stwv); 
        Reorder ro = new Reorder();
        ro.setInputFormat(data2); 
        ro.setOptions(weka.core.Utils.splitOptions("-R 1,3-last,2"));
        data2 = Filter.useFilter(data2, ro); 
        data2.setClass(data2.attribute(data2.numAttributes()-1)); 
        return data2; 
    }
    
    /**
     * AdTitle2body - puts the title of the article into the body text attribute. 
     *
     * @param  i where attributes are (id, lexisnumber, date, title, source, wordcount, text, match, _isFactChecking, _merged)
     * @return     i' where attributes are (id, lexisnumber, date, title, source, wordcount, title + text, match, _isFactChecking, _merged)
     */
    public static Instances addTitle2Body(Instances data) throws Exception{    
       Instances data2 = new Instances(data); 
       int textI = BODY_IND; //index of the text attribute 
       int titleI = TITLE_IND; 
       //data2.insertAttributeAt(new Attribute("titleAndText"), tempI); 
       for (int i = 0; i < data2.numInstances(); i++){ 
            Instance d = data2.instance(i); 
            String s = d.stringValue(TITLE_IND) + " " +  d.stringValue(BODY_IND); 
            d.setValue(BODY_IND, s); 
        }
       return data2;
    }
    
    /**
     * addTitleRepeat ads the title x number of times. Repeatedly calls the adTitle2Body
     * method. 
     *
     * @param  Instances 
     * @param   timex 
     * @return    Instances' 
     */
    public static Instances addTitleRepeat(Instances data, int times)
    {
        Instances data2 = new Instances(data); 
        
        for (int i = 0; i < data2.numInstances(); i++){ 
            String s = ""; 
            Instance d = data2.instance(i); 
            for (int j = 0; j < times; j++) { 
                s = s + d.stringValue(TITLE_IND); 
            } 
            d.setValue(BODY_IND, s + d.stringValue(BODY_IND)); 
        }
        return data2; 
    }
    
    /**
     * ReduceAttributes removes all attributes other than the lexis number, the body text, and the class att. 
     * Note: the trainingdataA/trainingdataB filters should be used on the Instances created by the 
     * labeled data class BEFORE this method is applied 
     */
    public static Instances reduceAttributes(Instances data) throws Exception { 
        Instances i = new Instances(data);   
        Reorder r = new Reorder();
        r.setInputFormat(i); 
        r.setOptions(weka.core.Utils.splitOptions("-R "+INDEX_ANALYSIS));
        i = Filter.useFilter(i, r);  
        return i ;
    }
 
    /**
     * TainingDataA builds the first model of the is relevent data 
     * @param data the  lexis data (with coding data merge in)
     * @param the data subset to include only the text and the is relevent data 
     */
    public static Instances trainingDataA(Instances data) throws Exception { 
        Instances i = new Instances(data);   
        Reorder r = new Reorder();
        r.setInputFormat(i); 
        r.setOptions(weka.core.Utils.splitOptions("-R "+INDEX_A));
        i = Filter.useFilter(i, r);  
        return i ;
    }

    /**
     * creates a model for "modelB" i.e. the model that is looking at the "is factchecking"
     * variable" 
     * @param data the lexis data 
     * @return data'
     */
    public static Instances trainingDataB(Instances data) throws Exception{ 
        Instances i = new Instances(data);
        for (int n = i.numInstances() - 1; n >= 0; n--) {
            if ((i.instance(n).value(i.numAttributes()-1) !=1 )) {
                i.delete(n);
            }
        }
        Reorder r = new Reorder();
        r.setInputFormat(i); 
        r.setOptions(weka.core.Utils.splitOptions("-R " + INDEX_B));
        i = Filter.useFilter(i, r); 
        return i ;
    } 
    
       //Static method removes instances that have duplicate values in all of the specified attribute fields 
   /**
    *  Removes the instances which have duplicate values for a given set of attributes 
    *  @param i  the data set 
    *  @param   indices of the attribute values which must be checked for duplicates 
    *  
    *  @return  a copy of i with duplicate instances removed 
    */
    public static Instances removeDuplicates(Instances i, int[] attributes) 
    {
        for( int a : attributes){
            i.sort(a);  
        } 
       List<Integer> remove = new ArrayList<Integer>(); 
       int pointer = 0; 
       String check = "";
       while(pointer<i.numInstances()) {
           String c = "" ;
           for ( int a : attributes) {
               c = c + i.instance(pointer).stringValue(a); 
            }  
            check = c; 
             for(int n = pointer+1; n< i.numInstances(); n++) { 
               String s = ""; 
               
                for (int a : attributes) { 
                    s = s + i.instance(n).stringValue(a); 
                } 
               
                if (s.equals(check)) {
                  i.delete(n); 
                  remove.add(n);    
                   n=n-1; 
                } 
            }
                pointer++; 
            }
            return i; 
        } 
        
    /**
     * m2oneMerge Completes merges Instances A with instances B where A.numInstances()> B.numInstances() 
     * for each instances where the value of the attribute matchA[n] = matchB[n]. 
     * Note: Since in the this program I will always be merging one two variables, title and date, 
     * the program assumes a two dimensional array for both matchA and matchB.
     * 
     * @param A larger dataset 
     * @param B  smaller dataset 
     * @param  matchA  two dimensional array of the indices of the variables in the dataset A to match on. 
     * @param matchB two dimensional array of the indices of the variables in the dataset B to match on. 
     * @param attributesB the attributes of b to keep 
     * @param missing string value to use in merge data if no values of B are availible 
     * 
     */
    public static Instances m2OneMerge(Instances dataA, Instances dataB, int[] matchA, int[] matchB, int[] attributesB, String missing ) 
    {
        Instances A = new Instances(dataA); 
        Instances B = new Instances(dataB); 
        FastVector mergeV = new FastVector(2); 
        mergeV.addElement("0") ; 
        mergeV.addElement("1"); 
        Attribute m = new Attribute("_merged", mergeV); 
        B.insertAttributeAt(m , B.numAttributes()); //records if the match in B got succesfully matched
        int atNumA = A.numAttributes(); //number of attributes in A, 
        int k  = atNumA; //counter for number of attributes 
        for (int i : attributesB) {
                 //creates a new atribute at the end of the attributes of A 
                //write now this only works for string attributes 
               if(B.attribute(i).isNumeric ()) {
                   A.insertAttributeAt(new Attribute("_"+B.attribute(i).name()), k);
                }
                else if (B.attribute(i).isNominal()){ 
                    FastVector f = new FastVector(); 
                    f.addElement("0"); 
                    f.addElement("1"); 
                    Attribute ai = new Attribute("_"+B.attribute(i).name(), f); 
                }
                else{
                    FastVector f = new FastVector(); 
                    Attribute ai = new Attribute("_"+B.attribute(i).name(), (FastVector) null);
                    A.insertAttributeAt( ai, k); 
                } 
                  k++;   
        }
        A.insertAttributeAt(m, A.numAttributes());
        // Now fill the attributes B 
        int iA = 0; 
        int latestB = 0; 
        int iB = 0; 
        boolean inData = true; 
        
        while(iA < A.numInstances()) { 
            boolean notFound = true;  
            while( iB< B.numInstances() & notFound) { 
              if ( B.instance(iB).stringValue(matchB[0]).contains(A.instance(iA).stringValue(matchA[0]))) {//this row is present 
                  B.instance(iB).setValue(B.numAttributes()-1, 1); //_merge = 1 in B
                // loop through and fill attributes 
                int j = 0; 
                for(int b : attributesB){ 
                 if (B.attribute(b).isNumeric()) { 
                     A.instance(iA).setValue(j+atNumA, B.instance(iB).value(b)); 
                    }
                 else{
                        A.instance(iA).setValue(j+atNumA, B.instance(iB).stringValue(b));
                    }
                }  
                 iB = 0; 
                 notFound = false; 
                 A.instance(iA).setValue(A.numAttributes()-1, 1); //set value of _merge = to 1 in A 
            }
             else{ 
                 iB++; 
                }
            }
           if(notFound) {
               for(int j = 0; j+ atNumA < k; j ++) {
                   A.instance(iA).setMissing(j+atNumA); 
               } 
               A.instance(iA).setValue(A.numAttributes() -1, 0); //set _merge = to 0 
                iB = 0; 
            }
               iA++; //regardless increment A     
        } 
        return A;
    }
    
    /**
     * Returns a string of the string representations of the value in each attribute of the instance i
     *
     * @param  i-the instance 
     * @return  the string representations for all the values of the attributes of this instance     
     */
    public static String instanceString(Instance i)
    {
        String s = ""; 
        for (int n = 0; n<i.numAttributes()-1; n++) { 
           s = s + i.toString(n) + "| ";   
        }
        s = s + i.toString(i.numAttributes()-1); 
        return s; 
    }
    
     /**
     * Retruns a string representations of the value of each attribute (a double) for the instance i  
     *
     * @param  i the instance 
     * @return  the values for each attribute of i  
     */
    public static String instanceValue(Instance i)
    {
        String s = ""; 
        for (int n = 0; n<i.numAttributes()-1; n++) { 
           s = s + i.value(n) + "| "; 
        }
        s = s + i.value(i.numAttributes()-1); 
        return s; 
    }
    
    /**
     * A utility method which prints the names of the attributes in a class  
     *
     * @param  i   
     * @return     void-but prints the names of the attributes of the dataset i 
     */
    public static void printAttributes(Instances i)
    {
        for (int k = 0; k<i.numAttributes(); k++){
            System.out.println(k + ": " + i.attribute(k).toString()); 
        }
    }
}

