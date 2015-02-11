
/**
 * This class  builds a model given training data iniarnnd an obejct from the preproccessor 
 * interface which specifies how the basic lexis-nexis training data should be processed 
 * for the analysis. Has methods to classify a given instance as well as to return the 
 * results of different evaluation tests.
 * 
 * @author Rachel Warren 
 * @version October 13 2013 
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
 import weka.classifiers.evaluation.ThresholdCurve; 
 import weka.classifiers.meta.ThresholdSelector; 
 import weka.core.SelectedTag; //weka class for selecting options in classifiers 
 import java.util.Random; 

 //import weka.attributeSelection.correlationAttributeEval; 

 
public class LexisClassifier
{
  
     private Instances trainingData; 
     private Classifier model; 
     private int index; //Class attribute 
     /*
      * How the training data and any instane that will be classified should be processed 
      */
     private Preprocessor format; 
     private boolean updated; 
     
    /**
     * Defualt Constructor for objects of the LexisClassifier Classs.
     * Uses the NaiveBeyesMultinomial classifier as a defualt and does no preprocessing 
     * (uses the noPreprocessor Preprocessor which simply returns the existing data 
     * @precondition: the data has been preprocessed before it is added to the training set. 
     * Not this precondition requires that any isntance classified with this model must be processed 
     * in the same way (which is not gurented). 
     * @param  training Data  
     * @param  i index of the class attribute 
     * 
     */
    public LexisClassifier(Instances td, int i) throws Exception 
    {
        this.index = i; 
        this.trainingData = td; 
        this.trainingData.setClass(trainingData.attribute(i)); 
        this.trainingData.setClassIndex(i); 
        this.model = (Classifier) new NaiveBayesMultinomial(); 
        this.format = (Preprocessor) new noPreprocessor(); 
        model.setOptions(weka.core.Utils.splitOptions("-D"));
        this.updated = false; 
  
    }
    
    /**
     * This constructor takes raw instance object and a preprocesses object. 
     * The preprocesor object must provide a way to clean the data. IF the Lexis 
     * classifier is constructed in this way then the classify methods can be fed un-preprocessed
     * lexis nexis instances 
     * 
     * 
     */
     public LexisClassifier(Instances td, Preprocessor p) throws Exception { 
         format = p; 
         trainingData = p.processData(td); 
         index = trainingData.classIndex();  
         model = (Classifier) new NaiveBayesMultinomial(); 
         updated = false; 
        } 
     
    /**
     * Constructs the a LexisClassifier object given any kind of classifier
     * @ param data the trainingData (must have existing class attribute).  
     * @ param i the index 
     * @ param Classifier
     */
    public LexisClassifier(Instances td, int i, Classifier c) throws Exception
    { 
        this.index = i; 
        this.trainingData = td; 
         this.trainingData.setClass(trainingData.attribute(i)); 
        this.trainingData.setClassIndex(i); 
        this.model = c; 
        model.setOptions(weka.core.Utils.splitOptions("-D"));
        this.format = (Preprocessor) new noPreprocessor(); 
        //this.classifierString = "weka.classifiers.bayes.NaiveBayes"; 
        
        this.updated = false; 
        
    }

    /**
     * classifies one instance and returns the value of the predicted class attribute 
     * precondition: must have the same attribute structure as the training data, but with missing values in the classIndex column
     * side affect: if the model is not updated, 
     *  it will be updated accroding to the data in the "training data" parameter 
     * 
     * @param new instance to classify 
     * @return  the class this instances is classified into 
     */
    public String classify(Instance unclassified) throws ConvertException{
       try{ unclassified = format.processOne(unclassified); 
            if (!this.updated){
               this.updateModel(); 
            } 
           
           double predicted = model.classifyInstance(unclassified); 
           return trainingData.classAttribute().value((int) predicted); 
        } 
        catch (Exception e) { 
            throw new ConvertException("Classification error: " + e.getMessage());
         }
        }
    
    /**
     * Given a set of instances, return a copy of the instances with the class 
     * attribute labeled according to this model
     *
     * @param  the unclassified instances
     * @return  a copy with the class attribute filled in. 
     */
    public Instances label(Instances unlabeled) throws Exception{
         if(this.updated == false) { 
             this.updateModel(); 
            } 
         Instances labeled = format.processData(unlabeled); 
          for (int i = 0; i < unlabeled.numInstances(); i++) {
              double clsLabel = this.model.classifyInstance(unlabeled.instance(i));
              labeled.instance(i).setClassValue(clsLabel); 
            }
          return labeled;
     }

    /**
     *  Returns the model with the current data 
     *
     * @return     the classifier 
     */
    public Classifier getModel()
    {
       return this.model;
    }
    
    /**
     * Builds the model using the current value of the trainingData field 
     */
    public void updateModel() throws Exception 
    {
        
        this.model.buildClassifier(this.trainingData); 
        this.updated = true; 
    }

 
    /**
     * Performs a 10 Fold Cross Validation of this classifier object using all the availible training data
     *
     * @param  fold >=2 the number of folds to generate 
     * @return     the evaluation object, 
     */
    public  Evaluation crossVal(long seed, int folds) throws Exception
    {

        Random rand = new Random(seed);   // create seeded number generator
        Instances randData = new Instances(this.trainingData);   // create copy of original data
        randData.randomize(rand);   
        randData.stratify(folds);         
        Random r = new Random(seed); 
        String[] options = {}; 
        Evaluation eTest = new Evaluation(this.trainingData) ;
        this.trainingData.setClassIndex(this.index); 
        eTest.crossValidateModel(this.model, this.trainingData, folds, r);   
        return eTest; 
    }
   /**
    * twoStepClassify takes a second classifier and unclassified instances, and classifies the data accordingly. 
    * @param two the second classifier 
    * @param data -the instances to be classified. 
    */
    public Instances twoStepClassify(LexisClassifier two, Instances data) throws Exception{ 
         Instances labeled = data;
        for(int i = 0; i < data.numInstances(); i++){  
                  
           FastVector classV = new FastVector(2); 
           classV.addElement("0") ; 
           classV.addElement("1"); 
           Attribute a1 = new Attribute("was_coded", classV); 
           Attribute a2 = new Attribute("_isFactChecking", classV); 
           labeled.insertAttributeAt(a1 , labeled.numAttributes());
           labeled.insertAttributeAt(a2, labeled.numAttributes());
           if(!this.updated){
               this.updateModel(); 
            }
           double predicted = this.getModel().classifyInstance(data.instance(i));
           String s = this.getTD().classAttribute().value((int) predicted); 
           if (s =="1") { 
                labeled.instance(i).setValue(labeled.numAttributes()-2, 1);
                double predicted2= two.getModel().classifyInstance(data.instance(i)); 
                String s2 = two.getTD().classAttribute().value((int) predicted); 
                labeled.instance(i).setValue(labeled.numAttributes()-1, s2); 
            } 
            else {
               // B.instance(iB).setValue(B.numAttributes()-1, 1)
                labeled.instance(i).setValue(labeled.numAttributes()-2, 0); //set was_coded to 0 
                labeled.instance(i).setValue(labeled.numAttributes()-1, 0); //set _isFactChecking to 0
            }
            
        }//end for loop 
        return labeled; 
    } 
    
    /**
     * Classifies a single instance according to both the is relevent and is factchecking model. Assumes
     * that the classifier that it takes as its parameter is trained to classify the factchecking variable. 
     * 
     * @param second    a nother classifier to classify the instances classified as one by THIS classifier
     * @param i     the instance to classify 
     * @return  a string representing the class value. 
     */
    public String twoStepClassify(LexisClassifier second, Instance i) throws ConvertException 
    {
   
           String s = this.classify(i);  
           if (s =="1") { 
                return second.classify(i); 
            } 
            else {
               return s; 
            }
            
    }


    /**
     * Return result of prediction. 
     * @return     a string representign whether the prediction is correct. Either 
     */
    public String isCorrect(Instance i) throws ConvertException {  
        String s = "";
        String predicted =classify(i);
        String actual = i.classAttribute().value((int) i.classValue());
        if(predicted == actual){
           s = "true ";
        }
        else { 
            s = "false "; 
        }
        if (predicted =="1"){ 
            s = s + "positive"; 
        } 
        else if (predicted == "0"){
            s = s  + "negative"; 
        } else { 
            s = "error" ;
        }
       return s;
        } 

    /**
     * ThresholdCurve returns an instance objedts with the results of the threshold curve for an evaluation. 
     *
     * @param  seed 
     * @param folds 
     * @return     Instances 
     */
    public Instances thresholdCurve(long seed, int folds) throws Exception
    {
        Evaluation eval = crossVal(seed, folds); 
        ThresholdCurve tc = new ThresholdCurve(); 
        return tc.getCurve(eval.predictions(), this.index); 
    }
    
    /**
      * Set the threshold of this classifier manually (according to input given by the user). 
      * 
     * @param manualThreshold the threshold to set for the probability 
     * @return an evaluation of the model when build with the new threshold 
     */
    public void setThresholdManual(double manualThreshold) throws Exception
    {
        ThresholdSelector tc = new ThresholdSelector(); 
        tc.setClassifier(this.model); 
        
        if (manualThreshold > 0) { 
            tc.setManualThresholdValue(manualThreshold); 
        } 
        else { 
           // throw ConvertException("threshold is out of range"); 
        } 
        this.model = tc; 
          
    }
    
    /**
     *  Optimizes for recall 
     */
    public void setThresholdRecall() throws Exception
    {
        ThresholdSelector tc = new ThresholdSelector(); 
        tc.setClassifier(this.model); 
        //these tags indicate which option to do in an internal weka format 
        //the values of the tags are static fields in the ThresholdSeletor class 
        SelectedTag crosValTag = new SelectedTag(ThresholdSelector.EVAL_CROSS_VALIDATION, ThresholdSelector.TAGS_EVAL); 
        SelectedTag measureTag = new SelectedTag(ThresholdSelector.RECALL, ThresholdSelector.TAGS_MEASURE); 
        tc.setEvaluationMode(crosValTag); 
        tc.setMeasure(measureTag); 
        this.model = tc; 
        
        //return crossVal(1, 10); 
    }
    
      /**
     *  Optimizes for fStatistic 
     */
    public void setThresholdFstat() throws Exception
    {
        ThresholdSelector tc = new ThresholdSelector(); 
        tc.setClassifier(this.model); 
        SelectedTag crosValTag = new SelectedTag(ThresholdSelector.EVAL_CROSS_VALIDATION, ThresholdSelector.TAGS_EVAL); 
        this.model = tc; 
        
        //return crossVal(1, 10); 
    }
    
    /**
     * @param i 
     * @param classNumber   the number of the class to get the probability for 
     *    (if a binary class should be one or zero) 
     */
    public double getDistributionForInstance( Instance i, int classNumber) throws Exception{
        //double[] cp =  this.model.distributionForInstance(i); 
        //return cp[classNumber]; 
        return 0.0;
    } 
 
     /**
     * ifCorrect returns a value for if the instnace is correct: 
     * -2: = false positive 
     * -1 = false negative 
     * 0 = true negative 
     * 1 = true positive 
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public int isCorrectInt(Instance i) throws ConvertException
    {
        
        String s = this.isCorrect(i) ; 
        if(s.equals("true positive")) {
            return 1; 
        } else if(s.equals("true negative")) { 
            return 0; 
        } else if(s.equals("false positive")) { 
            return -2; 
        } else if(s.equals("false negative")) {
            return -1; 
        } else { 
            throw new ConvertException("Classification error : " + s);  
        } 
    }

    /**
         * Getter method for the training data 

         * @return     training data 
         */
        public Instances getTD()
        {
            return this.trainingData;
        }

        public static void main(String args[]) throws Exception {        
     
        }//end main method 
  
   
  
    } //end class.
    
    
    
           
            
           


