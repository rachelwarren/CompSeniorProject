
/**
 * This class prints the evaluation matrix results of different threshold evaluations.at  
 * None
 * Fstat 
 * Precision 
 * Recall 
 * 
 * @author Rachel Warren 
 * @version 12-8-2013
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
public class ThresholdEvaluations
{
    public static final String FILE = "files/finalResults/ThresholdResults.txt";
    
    public static void main(String[] args) throws Exception{ 
        ResultsWriter pr1 = new ResultsWriter(FILE);
        String[] names = new String[3]; 
        String[] description = new String[3];
        //Labeled data 
        LabeledData l = new LabeledData(2008); 
        Instances rawData = l.buildData(); 
        rawData = InstanceUtils.trainingDataA(rawData); 
        ArrayList<LexisClassifier> modelListA = new ArrayList<LexisClassifier>() ;
        //build string to word vector 
        OptimalPreprocessor op = new OptimalPreprocessor(); 
        names[0] = "Stemmer";
        description[0] = "stoplist: yes, stemmer: yes, training size: random 10% , numeric word counts, default threshold 0.5";
        LexisClassifier bc = new LexisClassifier(rawData, op); 
        modelListA.add(bc); 
        names[1] = "Optimized_Recall" ; 
        description[1] = "stopList: yes, stemmer: no, training size: random 10% numeric word counts, threshold optimized for high recall"; 
        
        LexisClassifier recall =  new LexisClassifier(rawData, op); 
        recall.setThresholdRecall(); 
        modelListA.add(recall); 

        LexisClassifier fstat = new LexisClassifier(rawData, op); 
        fstat.setThresholdFstat(); 
        modelListA.add(fstat); 
        names[2] = "Optimized_Fstat" ;
        description[2] = "stopList: yes, stemmer: no, training size: random 10% numeric word counts, threshold optimized for high f statistic";
        try{
            pr1.writeLine("The following models are tested: " ); 
            pr1.modelKey(names, description);
            pr1.writeLine("The following table shows the results of a 10 fold cross validation of the model for several different pre processing strategies");  
            pr1.writeLine("Model A: classifies articles as relevent or not relevent, i.e. predicts the was coded variable " ) ;
            pr1.writeEvaluations(modelListA, names);
        } 
        finally{ 
            pr1.closeW(); 
        } 
    }
} 
        
         

