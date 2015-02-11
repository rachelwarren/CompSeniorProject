
/**
 * Custom Exceptions for this program which correspond to 
 * Weka exceptions associated with building the classifier
 * rather than user input exceptions 
 * 
 * @author Rachel Warren 
 * @version 12/14/2013 
 */
public class ConvertException extends Exception{ 
    
    public ConvertException() {
        super(); 
    } 
    
    public ConvertException(String message) { 
        super(message); 
    } 
    
    public ConvertException(String message, Throwable cause) { 
        super(message, cause); 
    } 
    
    public ConvertException(Throwable cause) { 
        super(cause); 
    } 
    
    
} 

