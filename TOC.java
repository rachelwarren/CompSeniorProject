
/**
 * The table of contents for the coding project. Sorts on title. And has methods 
 * to add article to the table of contents and to print them. 
 * 
 * @author Rachel 
 * @version 12-10-2012
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
public class TOC
{
    /**
     * filepath where the TOC called TableOfContents.txt will be written 
     */
    private String filepath;
    /**
     * The description of the model that this TOC is classifying 
     */
    private String header; 
    private List<TOCEntry> contents; 
    private int size; 
    private boolean updated; 
    public static String COLUMN_HEADER = 
       "id \t file_name \t aticle_Title \t article_Date \t Classified_as \t classProbability \t date_Classified"; 
    public static String FILENAME = "TableOfContents.txt";
    /**
     * Constructor for objects of class TOC
     */
    public TOC(){
        header = "Table of Contents for Lexis Model"; 
        contents = new ArrayList<TOCEntry>(); 
        size = 0; 
        filepath = "";
    }
    
     /**
     * Constructor for objects of class TOC
     * @param f the filepath ;
     * @param h a description of the table of contents 
     */
    public TOC(String f, String h){
        header = h; 
        contents = new ArrayList<TOCEntry>(); 
        size = 0; 
        filepath = f; 
    }

    /** 
     * Creates a TOC file and writes not entries to it 
     * @throws UIException if the file cannot be written 
     */
    public void createFile() throws UIException 
    { 
        try{ 
            File f = new File(filepath+"/" + FILENAME); 
            f.createNewFile(); 
            BufferedWriter w = new BufferedWriter(new FileWriter(f)); 
            try{ 
                w.write(header); 
                w.newLine(); 
                w.write(COLUMN_HEADER); 
                w.newLine(); 
            } 
            finally{ 
                w.close(); 
            } 
        }  
        catch ( IOException e){
           throw new UIException("Cannot write Table of Contents to filepath \" " +  filepath + ". ");  
        }   
    } 
    
    /**
     * Adds and article to the table of contents. 
     * 
     * @param a and article object to ad to the table of contents 
     */
    public void addEntry(Article a){ 
        TOCEntry e = a.createTOCEntry(this.size +1, a.getHtmlFile());
        contents.add(e); 
        size = size +1; 
        updated = false; 
    } 
    
    /**
     * Writes all the entries added to this TOC to a file. Will overwrite the table of contents if it already exists
     * if the boolean is true it will print the list of entries sorted in descending order according to the class 
     * probability. 
     * 
     * @param sort -weather the table of contents should sort on the class probability (sorts in descending order)    
     * @throws UIException if the table of contents has already been written OR if the file cannot be written to
     */
    public void writeTOC(boolean sort) throws UIException{ 
        if (sort){ 
         Collections.sort(contents); 
       }
       try{ 
            this.createFile(); 
            BufferedWriter w = new BufferedWriter(new FileWriter(this.filepath + FILENAME, true));
            try{ 
                w.newLine(); 
                for (TOCEntry t: contents){ 
                    w.write(t.toString()); 
                    w.newLine(); 
                } 
                updated = true; 
            }
            finally{ 
                w.close(); 
            }
        }
        catch (IOException e) {
            updated = false; 
           throw new UIException("Cannot write table of contents to given directory");   
        }  
    } //end method 
    
    /**
     * A message about where the table of contents was printed 
     * @return string message with the location of the table of contents
     */
    public String locationMessage()
    { 
        if (updated) { 
            return "The table of conents has been written to : " +this.filepath + FILENAME; 
        }
        else { 
          return  "The table of contents is not up-to-date.";
        } 
    }
  } //end class  
    
    

   
   

