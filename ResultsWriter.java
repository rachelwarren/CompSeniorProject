
/**
 * Results Writer generates a document to print the results in, saving 
 * a buffered writer to that document as a private field. It then has methods to 
 * write various different tables comparing models across different evaluation metrics 
 * 
 * @author Rachel Warren  
 * @version 12.04.2013
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
import java.text.SimpleDateFormat;

public class ResultsWriter
{
    // instance variables - replace the example below with your own
    private BufferedWriter w;
    
    public static String DEFUALT = "PrintResults.txt"; 
    public static int YEAR = 2008; 
    
    /**
     * Constructor for objects of class PrintResults, creates a DEFUALT file 
     */
    public ResultsWriter() throws IOException
    {
        //Creates a new file with the name of the static field DEFUALT 
        File output = new File(DEFUALT) ;
        //write the documents out to a file 
        output.createNewFile(); 
        w = new BufferedWriter(new FileWriter(output));
        
    }
    
    /**
     * Constructor for objects of class ResultsWriter, creates a file specified by the user 
     * @param filename 
     * 
     * throws IOException 
     */
    public ResultsWriter(String fileName) throws IOException
    {
        //Generate Today's Date 
        Date myDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String myDateString = sdf.format(myDate);
        //Creates a new file with the name  
        
        File output = new File(fileName) ;
        if (output.isFile()) { 
           FileWriter fw = new FileWriter(fileName ,true); 
           this.w = new BufferedWriter(fw); 
        } 
        else{ 
            FileOutputStream is = new FileOutputStream(output);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            this.w = new BufferedWriter(osw);
            w.write("File created : " + myDateString); 
            w.newLine(); 
        } 
        
        w.write("Results of Lexis Model generated : " + myDateString ) ;
        w.newLine();
        w.write("_______________________________________"); 
        w.newLine(); 
       
    }
    
     /**
     * Constructor for objects of class ResultsWriter, takes an existing bufferwriter 
     */
    public ResultsWriter(BufferedWriter bw) throws IOException
    {
        w = bw;
    }
    
    /**
     * modelKey prints the names and desciptions of a list of models 
     * 
     *@param names a list of the names of the models 
     *@param description- a list of the description of the model 
     */
    public void modelKey(String[] names, String[] description) throws IOException
    {
        int i = 0; 
        for (String n : names) { 
            String line = n + ": "+  description[i]; 
            w.write(line); 
            w.newLine(); 
            i++; 
        } 
        this.insertBreak(); 
    }
    
   
       /**
     *  writeEvaluations prints out the evaluation matrix for models made with several different variaties of string to word vectors
     * The parameter is the basic lexis data. 
     * @param String[] names 
     * @param modelList
     
     * @throws   weka exception is thrown if the classifier cannot be built   
     */
    public void writeEvaluations(ArrayList<LexisClassifier> modelList, String[] names) throws Exception {    
        ArrayList<String[]> modelsA  = new ArrayList<String[]>(); 
        String[] rowNames= {"modelName", "attributes", "correctly classified",
                "incorrectly classified",  "kappa score", "precision", "recall",
                "confusion matrix"} ;
        //build the models for each of the string to workvectors created 
        for (int i = 0; i < names.length; i++){ 
            String[] mA = new String[8];  
            String[] m = TestUtils.evaluationMatrix(names[i], modelList.get(i)); 
            modelsA.add(m);
        }  
        
            for(int j = 0; j < rowNames.length; j++) { 
                String printA = rowNames[j]; 
                for(int k = 0; k < modelList.size() ; k++) {
                    String[] column= modelsA.get(k) ;
                    printA = printA + ", " + column[j]; 
                }  
            w.write(printA); 
            w.newLine(); 
            w.flush(); 
        }
        this.insertBreak(); 
    }//end method 
    
    
    
      /**
     * Builds a classifier on a randomly selected 50% of the articles for each of the two data sets given as parameters, 
     * then clssifies each of the remaining instances in the dataSet using that classifier and returns the lexis numbers of 
     * training set, the false positives, false negatives, and the correctly classified articles. 
     * 
     * Precondition: string to word vector has already been applied to the two Instance objects
     * 
     * 
     * @param tempA - the data cleaned for the analysis of the first model, the is relvent variable 
     * @param tempB - the data cleaned for the analysis of the second model, the is factchecking variable 
     
     *  @throws   weka exception is thrown if the classifier cannot be built     
     * 
     */
    public void writeLexNums(Instances tempA, Instances tempB) throws Exception {
         
        Random r = tempA.getRandomNumberGenerator(1); 
        tempA.randomize(r) ;//shuffle the instances 
        tempB.randomize(r); 
        ArrayList<Integer>[] resultsA = TestUtils.getPredictionsByLexNum(350, tempA);  
        ArrayList<Integer>[] resultsB = TestUtils.getPredictionsByLexNum(100, tempB);
        w.newLine(); 
        w.write("MODEL A: is_relvent variable, trained on 350 (50% of total) random articles"); 
        w.newLine(); 
        w.newLine(); 
        w.write("TRAINING ARTICLES: " + resultsA[0].toString()); 
        w.newLine(); 
        w.write("FALSE POSITIVES: " + resultsA[1].toString()); 
        w.newLine(); 
        w.write("FALSE NEGATIVES" + resultsA[2].toString());  
        w.newLine(); 
        w.write( "CORRECT :" + resultsA[3].toString());  
        w.newLine();
        w.write("MODEL B: is_factchecking varaible, trained on 100 (50% of total) random articles"); 
        w.newLine(); 
        w.write("TRAINING ARTICLES: " + resultsB[0].toString()); 
        w.newLine(); 
        w.write("FALSE POSITIVES: " + resultsB[1].toString()); 
        w.newLine(); 
        w.write("FALSE NEGATIVES" + resultsB[2].toString());  
        w.newLine(); 
        w.write( "CORRECT :" + resultsB[3].toString());  
        
        this.insertBreak(); 
    }
    
    /**
     * AttributeInfo: prints an ordered array list of the attributes that are the highest predictor of the class attribute for 
     * each item in an arraylist of models. 
     * The result is a csv which can be interpreted as a matrix of the form: 
     *   ,model1 ,    ,       Model2 ................. 
     * rank ,gainRatio, attName, gainRatio, attName ......
     *  0, x          name0      y         name 
     *  1, x'         name1
     *  ...
     *  
     *  
     * @param modelList a list Instances objects representing different ways of preprocessing the data 
     * @param name  names of the models 
     * @param x - the number of attributes to print  
      
     * @throws   weka exception is thrown if the classifier cannot be built   
     */
    public void attributeInfo(ArrayList<LexisClassifier> modelList, String[] names, int x) throws Exception
    { 
        ArrayList<ArrayList<String>> apList = new ArrayList<ArrayList<String>>();  
        for (LexisClassifier m : modelList) { 
            ArrayList<String> ap = TestUtils.attPredictionsStr(m.getTD()); 
            Collections.sort(ap);
            Collections.reverse(ap);
            apList.add(ap); 
        }
        //there has to be a sorting step here, or we will have a problem. 
        String firstLine = " , " ;
        String secondLine = "Rank, " ;
        for (String n : names) {
              firstLine = firstLine + ", " + n +", "; 
              secondLine = secondLine + ",  Attribute, GainRatio"; 
            } 
        w.write(firstLine);
        w.newLine(); 
        w.write(secondLine); 
        w.newLine(); 
        for(int i = 0; i<x ; i ++) { 
            String s = ""+ i + ", ";  
            for ( ArrayList<String> apl : apList) { 
                s = s + ", "+ apl.get(i); 
            } 
            w.write(s); 
            w.newLine(); 
        } 
        this.insertBreak(); 
    }
    
    /**
     * Writes a single line using the buffer writer in the field "w" 
     * @param s the string to write 
     */
    public void writeLine(String s) throws IOException{ 
      w.write(s) ;
      w.newLine();      
    } 
    
    /**
     * Close the bufferwriter associated with this ResultsWriter
     */
    public void closeW() throws IOException{
        w.close(); 
    } 
    
    /**
     * insert a line break and a line t
     */
    public void insertBreak() throws IOException{ 
        w.newLine(); 
        w.write("_________________________________________________");  
        w.newLine(); 
        w.newLine(); 
    } 
    /**
     * Writes a table of models constructed using a different string to word vector 
     * 
     * @param rawData the lexis data processed 
     * @param stwv   an array of different vectors to use 
     * @param names the names of the models 
     * @param description   test comment about each model 
     * 
     * @throws Exception if the classifier cannot be built 
     */
    public void printSTWVTable(Instances rawData, StringToWordVector[] stwv, String names[], String[] description) throws Exception 
    { 
        ArrayList<LexisClassifier> ModelListA = new ArrayList<LexisClassifier>() ;
        ArrayList<LexisClassifier> ModelListB = new ArrayList<LexisClassifier>() ; 
        Classifier cModel = (Classifier) new NaiveBayesMultinomial(); 
         int[] ai = {1}; 
        
        for (int k = 0; k < stwv.length; k ++ ){    
            Instances tempA= InstanceUtils.trainingDataA(rawData); 
            Instances tempB = InstanceUtils.trainingDataB(rawData);  
              
            tempA = InstanceUtils.applySTWV(ai, stwv[k], InstanceUtils.reduceAttributes(tempA)); 
            tempB = InstanceUtils.applySTWV(ai, stwv[k], InstanceUtils.reduceAttributes(tempB));   
            //a defualt mulitnomialBayes classifier for the model a and model b data 
            LexisClassifier testA = new LexisClassifier(tempA, tempA.classIndex(), cModel); 
            LexisClassifier testB = new LexisClassifier(tempB, tempB.classIndex(), cModel);
            ModelListA.add(testA); 
            ModelListB.add(testB); 
        } 
        this.writeLine("The following models are tested: " ); 
        this.modelKey(names, description);
        this.writeLine("The following table shows the results of a 10 fold cross validation of the model for several different pre processing strategies");  
        this.writeLine("Model A: classifies articles as relevent or not relevent-- predicts the was coded variable " ) ;    
        this.writeEvaluations(ModelListA, names); 
        this.writeLine("Model B: is_factchecking varaible"); 
        this.writeEvaluations(ModelListB, names);  
    }
    
    /**
     * Prints a table of evaluations for models where the title is weighted different amounts 
     *@param rawData
     * param repArray a list of the number of times to repeat the title for each tests 
     * @param the stwv to apply 
     * @param names
     * @param description
     * precondition: |names| = |description|
     * 
     * @throws Exception    weka exception if the classifier cannot be built 
       */
    public void printTitleTable(Instances rawData, int[] repArray, StringToWordVector stwv, String[] names, String[] description) throws Exception { 
             
        ArrayList<LexisClassifier> modelListA = new ArrayList<LexisClassifier>() ;
        ArrayList<LexisClassifier> modelListB = new ArrayList<LexisClassifier>() ; 
        Classifier cModel = (Classifier) new NaiveBayesMultinomial(); 
        int[] ai = {1}; 
             
            for (int rep : repArray) 
            { 
                Instances data1 = InstanceUtils.addTitleRepeat(rawData, rep);
                Instances dataA= InstanceUtils.trainingDataA(data1); 
                dataA = InstanceUtils.reduceAttributes(dataA); 
                Instances dataB = InstanceUtils.trainingDataB(data1); 
                dataB = InstanceUtils.reduceAttributes(dataB); 
                dataA = InstanceUtils.applySTWV(ai, stwv, dataA); 
                dataB = InstanceUtils.applySTWV(ai, stwv, dataB); 
                LexisClassifier lcA = new LexisClassifier(dataA, dataA.classIndex(), cModel); 
                LexisClassifier lcB = new LexisClassifier(dataB, dataB.classIndex(), cModel); 
                modelListA.add(lcA); 
                modelListB.add(lcB); 
            }
            this.writeLine("The following models are tested: " ); 
            this.modelKey(names, description);
            this.writeLine("The following table shows the results of a 10 fold cross validation of the model for several different pre processing strategies");  
            this.writeLine("Model A: classifies articles as relevent or not relevent, i.e. predicts the was coded variable " ) ;
            this.writeEvaluations(modelListA, names);
            this.writeLine("Model B: is_factchecking varaible"); 
            this.writeEvaluations(modelListB, names);    
         
    }
        
   
    } 
    
    
