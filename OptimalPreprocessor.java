
/**
 * Optimal preproceser is the series of steps to be used in the classifier implement in the GUI. 
 * The preprocessing steps are: to add the weighted title, reduce the number of attributes for 
 * the analysis, and apply a string to word vector (stop words and custom stemming). Note: this does not 
 * do threshold optimization and it does not clean attributes for the first or second model. 
 * 
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


public class OptimalPreprocessor implements Preprocessor 
{
    
    private StringToWordVector stwv; 
    private int[] indexForSTWV; //the indices of the attributes which the stwv must be applied to
    /**
     * Constructor for objects of class OptimalPreprocessor
     * it builds the stringToWordVector which will be used by both methods 
     * (that way the a new filter need not be created every time the proccess method is called); 
     */
    public OptimalPreprocessor() throws Exception 
    {
         String[] stemList1 = {"ad", "advertisement", "ads" ,"advertisements", "commercial", "commercials", "spot", "spots" };   
         StemmerOverRide stem1 = new StemmerOverRide(stemList1);   
         StringToWordVector wStem1 = InstanceUtils.buildSTWV(); 
         wStem1.setStemmer(stem1); 
         wStem1.setUseStoplist(true); 
         this.stwv = wStem1; 
         int[] ai = {1}; 
         this.indexForSTWV =  ai; 
    }   
    
     /**
     * A method for cleaning a single instance 
     * @param  i    an instance to process 
     * @return      the processed instance 
     */
    public Instance processOne(Instance i) throws Exception {
       
        Instances dataSet = new Instances(i.dataset(), 1);
        dataSet.add(i); 
        //Instances dataSet2 = new Instances(dataSet); //so that the instance will not be mutated 
        Instances dataSet2 = this.processData(dataSet); 
        return dataSet2.instance(0); 
    
        
    } 
    
    /**
     * A method for cleaning an entire dataset 
     * 
     * @param  i    instances to process 
     * @return        the processed instances (ready for classification);
     */
    
    public Instances processData(Instances i) throws Exception { 
         Instances data = InstanceUtils.addTitleRepeat(i, 5); 
         data = InstanceUtils.reduceAttributes(data); 
         data = InstanceUtils.applySTWV(this.indexForSTWV, this.stwv, data); 
         return data; 
    } 
 
  
    public static void main(String[] args) throws Exception { 
       OptimalPreprocessor op = new OptimalPreprocessor(); 
       LabeledData l = new LabeledData(2008); 
       Instances rawData = l.buildData(); 
       Instances rawDataA = InstanceUtils.trainingDataA(rawData); 
       System.out.println(rawDataA.numInstances()); 
       Instances clean = op.processData(rawDataA); 
       System.out.println(rawDataA.numInstances()); 
       File output = new File("arff4GUI.arff"); 
       output.createNewFile(); 
         BufferedWriter w = new BufferedWriter(new FileWriter(output));
       try{ w.write(clean.toString()); 
        } 
        finally{ 
       w.close(); 
    } 
       
    } 
}
