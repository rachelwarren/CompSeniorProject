
/**
 * This class contains a main method which creates CodingProject object and then adds all of the articles 
 * corresponding to the state level news wires to it.
 * 
 * @author Rachel Warren 
 * @version 12/14/2013 
 */

import java.text.*;
import java.util.*; 
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import weka.core.Utils;
import weka.core.Instance; 
import weka.core.Instances;

public class classifyNewsWires
{
    /**
     * main method builds the data, and classifies the articles 
     * 
     * @thros weka Exception if the classifier cannot be built 
     */
    // instance variables - replace the example below with your own
   public static void main(String[] args) throws Exception { 
       
        String rootDir = "files/2008/newsWireClassification2/";
        String path = "files/2008/newsWires/"; 
        String[] files = {"C.HTML", "F.HTML", "I.HTML", "M.HTML", "N.HTML",
          "NC.HTML", "NH.HTML", "O.HTML", "P.HTML", "V1.HTML", "V2.HTML"};   
       
        CodingProject project = new CodingProject(rootDir, 2008);
       for (String f :  files ) { 
           String lexisdoc = path + f; 
           project.classifyArticles(lexisdoc);
        }
          //print table of contents 
       project.returnTOC(false, true); 
           
       
        
       
        
       
    } 
}
