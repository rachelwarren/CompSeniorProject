import weka.core.Instance; 
import weka.core.Instances; 

/**
 * Preprocessor takes a single instance object which is the result of the lexis parser
 * and does all the nessesary steps to ready it to be classified. 
 * 
 * @author Rachel Warren 
 */
public interface Preprocessor
{
    /**
     * A method for cleaning a single instance 
     * @param  i    an instance to process 
     * @return        the processed instance 
     */
    Instance processOne(Instance i) throws Exception;
    
    /**
     * A method for cleaning an entire dataset 
     * 
     * @param  i    instances to process 
     * @return        the processed instances (ready for classification);
     */
    
    Instances processData(Instances i) throws Exception; 
    
}
