
/**
 *Main method to print the evaluation from the ResultsWriter class which will be used in the final
 *paper. Three files are created 
 *One: stemmer and stopword evaltuation 
 *Two: custom stemming vs. defualt stemmign 
 *Three: repeated titled 
 * 
 * @author Rachel Warren 
 * @version 12/14/2013
 */

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

public class printFinalModels
{
    public static final String FILE_ONE = "files/finalResults/STWVtables.txt"; 
    public static final String FILE_TWO = "files/finalResults/stemmerTables.txt"; 
    public static final String FILE_THREE = "files/finalResults/titleTables.txt"; 
    public static final int YEAR = 2008;
    /**
     * Prints all of the results tables that will be used in the paper 
     * 
     * @throws Exception 
     */
   
   public static void main(String[] args) throws Exception { 
        //build the basic lexis data, will be used for all the test 
       LabeledData l = new LabeledData(YEAR); 
       Instances rawData = l.buildData(); 
       //writes the stwvTables 
       ResultsWriter writer1 = new ResultsWriter(FILE_ONE);
       
       ResultsWriter writer2 = new ResultsWriter(FILE_TWO ) ;
       ResultsWriter writer3 = new ResultsWriter(FILE_THREE); 
       int numModels = 4; //the number of models to test  
       String[] names = new String[numModels];
       String[] description = new String[numModels];
       StringToWordVector[] stwv = new StringToWordVector[4]; //only five diff. stwv.  
       StringToWordVector defualt = InstanceUtils.buildSTWV(); 
       defualt.setUseStoplist(false); 
       stwv[0] = defualt; 
       names[0] = "Defualt"; 
       description[0] = "stoplist: no, stemming: no , stem exceptions: NA, training size: random 10%, numeric word counts"; 
       StringToWordVector stopListOnly = InstanceUtils.buildSTWV(); 
       stopListOnly.setUseStoplist(true); 
       stwv[1] = stopListOnly; 
       names[1] = "StopList_Only";
       description[1] = "stoplist: yes, stemmer: no, stem exceptions: NA , training size: random 10% , numeric word counts";
       StringToWordVector stemOnly = InstanceUtils.buildSTWV(); 
       stemOnly.setUseStoplist(false); 
       StemmerOverRide stemBasic = new StemmerOverRide();    
       stemOnly.setStemmer(stemBasic); 
       stwv[2] = stemOnly;
       names[2] = "Basic_stemming_only";
       description[2] = "stoplist: no, stemmer: yes, stem exceptions: none, training size: random 10%, numeric word counts ";
       
       StringToWordVector stemStop = InstanceUtils.buildSTWV(); 
       stemStop.setStemmer(stemBasic); 
       stemStop.setUseStoplist(true); 
       stwv[3] = stemStop; 
       names[3] = "Stoplist_&_Stemmer";
       description[3] = "stoplist: yes, stemmer: yes, stem exceptions: none, training size: random 10%, numeric word counts ";
        
       String[] names2 = new String[2]; 
       String[] description2 = new String[2]; 
       StringToWordVector[] stwv2 = new StringToWordVector[2]; 
       //Create the second set of String to word vectors to test stemmer 
       names2[0] = "Basic_Stemmer"; 
       description2[0] = description[3]; 
       stwv2[0] = stwv[3] ;
       names2[1]= "Custom_Stemmer";
       String[] stemList1 = {"ad", "advertisement", "ads" ,"advertisements", "commercial", "commercials", "spot", "spots" };   
       description2[1] =  "stoplist: yes, stemmer: yes, stem exceptions: yes*, training size: random 10%, numeric word counts";

        StringToWordVector customStemmer = InstanceUtils.buildSTWV(); 
        StemmerOverRide stem1 = new StemmerOverRide(stemList1);   
        StringToWordVector wStem1 = InstanceUtils.buildSTWV(); 
        wStem1.setStemmer(stem1); 
        wStem1.setUseStoplist(true); 
        stwv2[1] = customStemmer; 
        int titleSize = 4; //the size fo the table for the titles 
        String names3[] = new String[titleSize] ;
        String description3[] = new String[titleSize]; 
        names3[0] = "title_repeat_1";
        description3[0] = " custom stemming and stopwords removal, title is repeated once";
        names3[1] = "title_repeat_3";
        description3[1] = " custom stemming and stopwords removal, title is repeated thee times";
        names3[2] = "title_repeat_5";
        description3[2] = " custom stemming and stopwords removal, title is repeated five times";
        names3[3] = "title_repeat_10";
        description3[3] = " custom stemming and stopwords removal, title is repeated ten times"; 
        int[] repArray = {1, 3, 5, 10} ;
        try{ 
            writer1.printSTWVTable(rawData, stwv, names, description); 
            writer2.printSTWVTable(rawData, stwv2, names2, description2); 
            writer2.writeLine("*Stemmer Exceptions: " + Arrays.toString(stemList1));
            writer3.printTitleTable(rawData, repArray, stwv2[1], names3, description3); 
        }
        finally{ 
            writer1.closeW();
            writer2.closeW(); 
            writer3.closeW(); 
        }
    } 
    
}
