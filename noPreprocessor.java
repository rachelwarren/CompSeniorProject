
/**
 * A preprocessor object which simply returns the same instances object i.e. does no preprocessing
 * 
 * @author Rachel Warren 
 * @version 12/10/2013 
 */
import weka.core.Instances;
import weka.core.Instance;

public class noPreprocessor implements Preprocessor
{
    // instance variables - replace the example below with your own
    

    /**
     * Constructor for objects of class noPreprocessor
     */
    public noPreprocessor()
    {
  
    }

    /**
     * A method for cleaning a single instance 
     * @param  i    an instance to process 
     * @return        the processed instance 
     */
    public Instance processOne(Instance i) throws Exception {
        return i;  
    } 
    
    /**
     * A method for cleaning an entire dataset 
     * 
     * 
     * @param  i    instances to process 
     * @return        the processed instances (ready for classification);
     */
    
    public Instances processData(Instances i) throws Exception { 
         return i; 
    } 
    
}
