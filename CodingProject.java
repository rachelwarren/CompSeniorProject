
/**
 * The master class for the user's clasification project. Creates the directories for the 
 * two classes and the table of contents and classifies the articles 
 * (from an HTML file provided by user) 
 * and writes them to the directories accordingly.  
 * 
 * @author Rachel Warren 
 * @version 12-1-2013
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

public class CodingProject
{
    /*
     * This is the path to the directory where articles assigned class 0 are assigned (do not code)
     */
    private String pathClass0;
    /*
     * Path to the directory assigned class 1; 
     */
    private String pathClass1; 
    private TOC contents; 
    private LexisClassifier model1;
    //private LexisClassifier model2; 
    private List<Article> failures; 
   
    private int total0; //total number of articles to Ignore 
    private int total1; //total number of articles to Code 

    /**
     * Constructor for objects of class CodingProject
     * @rootDirectory   the path name of the directory to create the different files in 
     * @year    the year for the analysis 
     * 
     * @throw ConvertException if the training data cannot be build 
     */
    public CodingProject(String rootDirectory, int year) throws ConvertException, UIException 
    {
         boolean factChecking = false; 
           if(!factChecking){
                 pathClass0 = rootDirectory+"notRelevent"; 
                 pathClass1 = rootDirectory+"toCode";
                }
           else{ 
               pathClass0 = rootDirectory+"not_FactChecking"; 
               pathClass1 = rootDirectory+"FactChecking"; 
            }
           File toCode = new File(pathClass1); 
           File notRelevent = new File(pathClass0);
           if(!toCode.isFile()){ 
             toCode.mkdirs(); 
            } 
           if (!notRelevent.isFile()){
               notRelevent.mkdirs();
            }         
           Date today = new Date();
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           String dateString = sdf.format(today);
           contents = new TOC(rootDirectory, "Table of contents for Lexis Coding Project creatd " + dateString);
           try{ 
               LabeledData l = new LabeledData(year); 
               Instances rawData = l.buildData(); 
               Preprocessor p = (Preprocessor) new OptimalPreprocessor(); 
               Instances rawDataA = InstanceUtils.trainingDataA(rawData);
               
               this. model1 = new LexisClassifier(rawDataA, p);  
               if(factChecking) { 
                   Instances rawDataB = InstanceUtils.trainingDataB(rawData); 
                   //this.model2 = new LexisClassifier(rawDataB, p); 
                   
                } 
                else{ 
                    //this.model2 = null; 
                }
            } 
            catch (Exception e) { 
                throw new ConvertException("weka classifier exception: " + e.getMessage()); 
            } 
         failures = new ArrayList<Article>(); 
         this.total0 = 0;
         this.total1 = 0; 
        }
  
        /**
         * @param lexisdoc  the file path of the lexis document 
         * 
         * @return  a string message about how many articles were classified into each folder 
         */
        
    public String classifyArticles(String lexisdoc) throws UIException {   
         //Read Html Document 
         int toCode = 0; 
         int ignore = 0; 
         int failed = 0; 
         int counter = 0; 
         /*
          * The dummy lexis file contains the header information for a standard lexis document 
          */
         File lexis = new File(lexisdoc); 
         try{
             BufferedReader reader = new BufferedReader( new FileReader("files/dummyLexisArff.arff"));
             Instances header = new Instances(reader); 
             Scanner in = new Scanner(lexis); 
             String next = in.nextLine();
             while (in.hasNextLine())  {
                 boolean inDoc = false;
                 if  (next.contains("<DOCFULL>")){
                     inDoc = true;
                   }
                    String articleHTML = " ";   
                while(inDoc) {
                   next= in.nextLine(); 
                   if (next.contains("DOCFULL")){
                      inDoc = !inDoc; 
                      counter++; //increment article counter 
                      String aName = "article" + counter; 
                      Article a = new Article(model1, articleHTML, header, aName, lexisdoc);  
                            if(a.getClassValue() == "0") {
                              a.writeArticle(pathClass0);
                              ignore ++;//increment the number of articles which were classified as "0"
                              contents.addEntry(a); 
                            } 
                          else if(a.getClassValue() == "1") { 
                              a.writeArticle(pathClass1); 
                              toCode++;  //increment the number of article classified as "1" 
                              contents.addEntry(a); 
                            } 
                          else{
                             failed ++; 
                             this.failures.add(a); 
                            }
                    } 
                    else {
                       articleHTML = articleHTML + next ; 
                    }                      
                } //end while
                 next = in.nextLine();  
             } //end while 
               System.out.println("Classification of articles in file \"" + lexisdoc + "\" completed."); 
                reader.close(); 
            } //end try 
            catch (IOException e){ 
                  throw new UIException("Could not read \"" + lexisdoc + ". " + e.getMessage()); 
               }   
            catch (Exception e) { 
                 e.printStackTrace();
                throw new UIException("Weka exception: " + e.getMessage() + ", "  + "when classifying doc: " + lexisdoc + "." ); 
                        }
           this.total0 = this.total0 + ignore; 
           this.total1 = this.total1 + toCode; 
          return (counter + " Articles were parsed. From the filed: " + lexisdoc+" \n " + toCode
                      + " were classified as relevent and were written to the folder \""  + pathClass1 + "\". \n"
                      + ignore + " were classified as not relevent and written to the folder \"" 
                      + pathClass0 + "\". \n") ;
        } //end method     
    
   
    /**
     * Classifies all of the html documents in a folder.
     *
     * @param  path the location of the directory (must end with "/" 
     * @return     a string array describing the reults of each additions 
     */
    public ArrayList<String> addDirectory(String path){
       File folder = new File(path); 
       File[] listOfFiles = folder.listFiles(); 
       ArrayList<String> results = new ArrayList<String>(); 
       
       for (File f : listOfFiles) { 
           String lexisdoc = f.getName(); 
           System.out.println(f.getName()); 
           try{ 
               results.add(classifyArticles(lexisdoc)); 
            } 
           catch (UIException e){ 
              results.add(e.getMessage());  
            } 
        }
       return results; 
    }
    
    /**
     * TOC the table of contents 
     * @param sorted   whether the table of contents should be sorted 
     * @param includedFailed    whether the articles that were not successfully classified should be included in the 
     */
    public String returnTOC(boolean sorted, boolean includeFailed){ 
         try{ 
             if(includeFailed) { 
               for (Article a : failures) {
                  contents.addEntry(a); 
                }
            }
            contents.writeTOC(sorted); 
           return contents.locationMessage(); 
          }
          catch (UIException e){ 
              return e.getMessage(); 
           } 
       } 
    /**
      * Returns the number of articles succesfully classified so far "to code" 
      */     
    public int getNumCode(){ 
         return this.total0;
        }
        
    /**
     * Returns the number of articles that were classified as not to code 
     */
    public int getNumIgnore(){
        return this.total1;
    }
        
   public static void main(String[] args) throws Exception { 
       

    } 
 } //end class 
    
    
  

    
