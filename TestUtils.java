
/**
 * TestUtils contains static methods for formatting and printing different evaluations 
 * of the LexisClassifier. 
 * the results are written in the methods in the class ResultsWriter 
 * 
 * @author Rachel Warren 
 * @date 12/14/2013
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
    import weka.attributeSelection.ASEvaluation;
    import weka.attributeSelection.*; 
    
public class TestUtils
{

    /**
     * EvaluationMatrix builds an evaluation on the instances and returns a string array of the following 
     * statitics about that classifier 
      
     *
     * @param  name the name of the model 
     * @param LexisClassifier   the classifier to test (recall that a LexisClassifier carries the training 
     * data with it. 
     * 
     * @return an array with the results of various measures of a 10 fold cross validation.
     * 
     *  a[0] model name, 
     *  a[1] #correctly classified
     *  a[2]#incorretly classified
     *  a[3]precision
     *  a[4]recall
     *  a[5] the confusion matrix)
     * 
     */
 
    public static String[] evaluationMatrix(String name, LexisClassifier test) throws Exception{ 
        
        int[] ai = {1};  //the index of the text attribute 
        String[] a1 = new String[8];
        a1[0] = name ;
        a1[1]  = ""+ test.getTD().numAttributes(); 
        //Classifier cModel = (Classifier)new NaiveBayes();   
        Evaluation s = test.crossVal(1, 10);  
        a1[2] = ""+s.correct(); 
        a1[3] = ""+s.incorrect(); 
        a1[4] = ""+s.kappa(); 
        //positive result is class index of 1 
        a1[5] = ""+s.precision(1); 
        a1[6] = ""+s.recall(1);   
        a1[7] = ""+s.toMatrixString(); 
        return a1; 
        
        
    }
    /**
     * Same as test model, only assumes that defualt multinomial NaiveBayes model 
    
     * @param  name the name of the model 
     * @param i the data 
     * @return  the result of the test
     */
    public static String[] evaluationMatrix(String name, Instances i ) throws Exception
    {
        Classifier cModel = (Classifier) new NaiveBayesMultinomial(); 
        LexisClassifier test = new LexisClassifier(i, i.classIndex(), cModel); 
        return evaluationMatrix( name, test); 
    }

      /**
     * Get Predictions prints a list of false postives/negatives and correct predictions by lexis number 

     * @param b high end of the range of articles to be in the training set 
     * @return    an array list of the lexis numbers   
     *      a[0] = training articles 
     *      a[1] = false positives 
     *      a[2] = false negatives 
     *      a[3] = correct predictions 
     */
    public static ArrayList<Integer>[] getPredictionsByLexNum(int b, Instances data) throws Exception
    {
        Instances  data2 = data;
        Random r = data2.getRandomNumberGenerator(1); 
        data2.randomize(r);
        Instances td = new Instances(data2, 0, b); //make the training data 
        ArrayList<Integer> trainNumbers = new ArrayList<Integer>(); 
        for (int n = 0; n < td.numInstances(); n++ ) { 
            Instance in = td.instance(n); 
            String  v  = in.toString(0); 
            int id = Integer.parseInt(v);
            trainNumbers.add(id); 
        } 
        LexisClassifier test = new LexisClassifier(td, td.classIndex());
        ArrayList<Integer> falseP  = new ArrayList<Integer>();  
        ArrayList<Integer> falseN  = new ArrayList<Integer>();  
        ArrayList<Integer> correct = new ArrayList<Integer>(); 

        for (int i = b+1; i < data2.numInstances(); i ++) {
            int result = test.isCorrectInt(data2.instance(i));
            String  v  = data2.instance(i).toString(0); 
            int id = Integer.parseInt(v); 
            if (result >=0){ 
                correct.add(id); 
            } 
            else if(result == -2) { 
                falseP.add(id); 
            } 
            else { 
                falseN.add(id); 
            }
        }
        ArrayList<Integer>[] result = (ArrayList<Integer>[]) new ArrayList[4]; 
        result[0] = trainNumbers;
        result[1] = falseP;
        result[2] = falseN; 
        result[3] = correct; 
        return result; 
    }
    
     /**
     * This returns an array of doubles corresponding to the index of each attribute with the
     * Information gain ratio 
     * attribute of the training data  
     *
     * @param 
     * @return     the predictions for each attribute 
     */
    public static double[] attPredictions(Instances trainingData) throws Exception 
    {
        GainRatioAttributeEval CAE =  new GainRatioAttributeEval(); 
        CAE.buildEvaluator(trainingData); 
        int n = trainingData.numAttributes(); 
        double[] result = new double[n]; 
        for(int i = 0; i < n; i++) 
        {
            try { 
                double r = CAE.evaluateAttribute(i);
                result[i] = r; 
            }
            catch (Exception e) 
               {
                result[i] = 0-9.0;
            }
        }
        return result; 
    }
    

    /**
     * Generates a string with the variable name and its information gain ratio  
     *
     * @param  Data 
     * @return    a string with the attribute name and the value of the prediction. 
     */
    public static ArrayList<String> attPredictionsStr(Instances trainingData) throws Exception 
    {
        double[] p = attPredictions(trainingData); 
        ArrayList<String> s  = new ArrayList<String>();
        for (int i = 0; i < p.length; i++) {
            double d = p[i]; 
            if(d > 1-8.0){     
                String tupple = d+ ", " + trainingData.attribute(i).name(); 
                s.add(tupple); 
            }
        }
        return s; 
    }
    

}
