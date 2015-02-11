


/**
 * Article defines a single article object, which is constructed from a string of html text. 
 * It is used both to build a relation object, inorder to construct the initial training data and also to classify new articles 
 * for the GUI classifier. 
 * It has methods to classify the article given a model, and to write to a TOC.  
 * 
 * @author Rachel Warren 
 * @version 12-10-2013 
 */

import weka.core.stemmers.*;
import weka.core.FastVector; 
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute; 
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
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Tag; 
import java.text.*;
import weka.core.Utils;
//import lexisModel; 

public class Article
{
    /**
     * The model used to classify the article (the model also contains the information about how the 
     * instance was classified.
     */
    private LexisClassifier model; 
    private String location; 
    private String htmlText; 
  
    private String htmlFile; 
    /**
     * class value is equal to 0 or 1 unless it is missing (then it is set to "?") 
     */
    private String classValue; //how the article was classified 
    private String docName; 
    
    /**
     * variables corresponding to the instance information 
     */
     private int lexnum;
    //Date is in YYYY-MM-DD Format 
    private String date; 
    private String source;
    private int wordcount;
    private String title; 
    private Element body; 
    
    /**
     * Defualt Constructor for objects of class article
     */
    public Article()
    {
       
       htmlText = ""; 
       this.htmlFile = ""; 
       classValue = "un classified"; 
       location = ""; 
       model = null; 
       docName = "";
       
        // initialise arff fields 
        this.lexnum = 0 ;
        this.date = "2012-01-01";
        this.source = "Newspaper";
        this.wordcount = 0;
        this.title = ""; 
        Tag t = Tag.valueOf("div" ); 
        this.body = new Element(t, "") ; //empty element in JSoup
        
       
    }
    /**
     * Simple constructor for the article class used to build a lexis relation,
     * does not classify the article, but simply extracts the information 
     * to make an Instance object from the an HTML string 
     * 
     * @param htmlText  the html text of a single article 
     * @throws Exception is if the article text cannot be parsed 
     */
     public Article(String text)  {
         new Article(null, text, null, "defualt" , ""); 
         
        } 
     /**
     * Constructor for the article used for the Classify articles method in the GUI. 
     * 
     * @param Preprocessor   an object containing a method which can preprocess the instance for the model
     * @param htmlText   allready processed into a descreate article 
     * @param header    an Instance object with the basic lexis header (can be have no instances)
     * @param name  the names of the file the article will be written to (must end in.txt) 
     * that will be used to format this article as an Instance 
     * handles convertException if the article cannot be classified, and sets the class value equal to missing. 
     */
    public Article(LexisClassifier model, String text, Instances header, String name, String hf) 
    {
        htmlText = text; 
        this.htmlFile = hf; 
        this.location = "File_not_written";  
        this.docName = name; 
        //fill in arff fields 
        Document doc = Jsoup.parseBodyFragment(htmlText);
        Elements lines = doc.getElementsByTag("DIV");       
         //Extract the Lexis number 
        String lineLexNum = lines.get(0).text();      
        this.lexnum = getNum(lineLexNum); 
        //Extract the source name 
        this.source  = lines.get(1).text();        
        //The loop bellow checks the elements and then parses them accordingly 
        String t = "No title found"; //Defualt title 
        String d = "No Date found"; 
        int wc = 0; //The word count, to be set in the loop, defualt is 0. 
        String b = "None" ;//Defualt value of body text 
        int n = 1; 
        int size = lines.size(); 
        Element bodyTemp = null; 
        while(n < size) {
            Element l = lines.get(n);
            String s = l.text(); 
            //Date 
           try{
                if(l.className().contains("c3")){ 
                     String[] split = s.split("(, )(\\d)");
                      d = s.substring(0, split[0].length()+6);             
                } 
            }
            catch (Exception e) {
                d = " ";
            }
            //Title 
            if (l.child(0).classNames().contains("c5")){
                if(l.child(0).child(0).classNames().contains("c6")){
                    t = s; 
                }
                }
            //word count 
            if (s.contains("LENGTH:")) {
                wc = Article.getNum(s); 
                bodyTemp = lines.get(n+1); //usually the body is after the length 
            }  
            //body
            if (l.child(0).classNames().contains("c8")) {
                b = s;
                this.body = l; 
                n = size; //break out of the loop this is the last piece of useful information 
            }
             n++ ;
        }
 
       this.title = t; 
       this.date= dateParse(d); 
        //Word count
       this.wordcount = wc; 
       //Body text 
       if (b == "None") { 
           this.body = bodyTemp; //if the body wasn't in the normal format it is in most cases the element after the length 
        } 
       if (model == null) {
           classValue = "?"; 
        } 
        else {
             try{ 
               Instances copy  = new Instances(header, 0, 1); 
               Instance i  = copy.instance(0); 
               i.setValue(0, (double) lexnum);
               try{ 
                   i.attribute(1).parseDate(this.date); 
                } 
               catch (ParseException e) { 
                   i.setMissing(1); 
                } 
               i.setValue(2 , Utils.quote(this.title)); 
               i.setValue(3,  Utils.quote(this.source)); 
               i.setValue(4, (double) wordcount); 
               i.setValue(5,  Utils.quote(body.text())); 
               this.classValue =  model.classify(i);     
             } 
            catch (Exception e){ 
               System.out.println(e.getMessage()); 
               classValue = "?"; 
              } 
        } 
    }

     /**
     * Constructor for article's where we want to classfiy according to the is factchecking variable. 
     * @param model1     the is relevent model 
     * @param model2    the factchecking model 
     * @param Preprocessor   an object containing a method which can preprocess the instance for the model
     * @param htmlText   allready processed into a descreate article 
     * @param header    an Instance object with the basic lexis header (can be have no instances)
     * @param name  the names of the file the article will be written to (must end in.txt) 
     * that will be used to format this article as an Instance 

     */
    /*
    public Article(LexisClassifier model1, LexisClassifier model2,  String text, Instances header, String name, String hf) throws Exception 
    {
        htmlText = text; 
        this.htmlFile = hf; 
        this.location = "File_not_written";  
        this.docName = name; 
        //fill in arff fields 
        Document doc = Jsoup.parseBodyFragment(htmlText);
        Elements lines = doc.getElementsByTag("DIV");       
         //Extract the Lexis number 
        String lineLexNum = lines.get(0).text();      
        this.lexnum = getNum(lineLexNum); 
        //Extract the source name 
        this.source  = lines.get(1).text();        
        //The loop bellow checks the elements and then parses them accordingly 
        String t = "No title found"; //Defualt title 
        String d = "No Date found"; 
        int wc = 0; //The word count, to be set in the loop, defualt is 0. 
        String b = "None" ;//Defualt value of body text 
        int n = 1; 
        int size = lines.size(); 
        Element bodyTemp = null; 
        while(n < size) {
            Element l = lines.get(n);
            String s = l.text(); 
            //Date 
           try{
                if(l.className().contains("c3")){ 
                     String[] split = s.split("(, )(\\d)");
                      d = s.substring(0, split[0].length()+6);             
                } 
            }
            catch (Exception e) {
                d = " ";
            }
            //Title 
            if (l.child(0).classNames().contains("c5")){
                if(l.child(0).child(0).classNames().contains("c6")){
                    t = s; 
                }
                }
            //word count 
            if (s.contains("LENGTH:")) {
                wc = LexisInstance.getNum(s); 
                bodyTemp = lines.get(n+1); //usually the body is after the length 
            }  
            //body
            if (l.child(0).classNames().contains("c8")) {
                b = s;
                this.body = l; 
                n = size; //break out of the loop this is the last piece of useful information 
            }
             n++ ;
        }
 
       this.title = t; 
       this.date= dateParse(d); 
        //Word count
       this.wordcount = wc; 
       //Body text 
       if (b == "None") { 
           this.body = bodyTemp; //if the body wasn't in the normal format it is in most cases the element after the length 
        } 
       
        try{
           Instances copy  = new Instances(header, 0, 1); 
           Instance i  = copy.instance(0); 
           i.setValue(0, (double) lexnum);
           try{ 
               i.attribute(1).parseDate(this.date); 
            } 
           catch (ParseException e) { 
               i.setMissing(1); 
            } 
           i.setValue(2 , Utils.quote(this.title)); 
           i.setValue(3,  Utils.quote(this.source)); 
           i.setValue(4, (double) wordcount); 
           i.setValue(5,  Utils.quote(body.text()));  
           
           //if there is no second model classify only according to the first model  
           if (model2 != null){
              classValue =  model1.twoStepClassify(model2, i);
            }
            else{ 
                 
                classValue =  model1.classify(i); 
            } 
        } 
        catch (Exception e){ 
           System.out.println(e.getMessage()); 
           classValue = "?"; 
         
          } 
          
       
    } */
    
    /**
     * Restuns a string representing the arff instance format 
     */
    public String toARFFString()
    {
        return "" + lexnum + ", " + "\""+ date.toString()+ "\"" + ", " +Utils.quote(title) +", "+ Utils.quote(source) +  ", " + wordcount + ", " + Utils.quote(body.text()) + ", ?"  ;
    }
   
    
    
    /**
     * note: will write the file even if the class value is missing 
     */
    public void writeArticle(String l) throws UIException{ 
       
       File output = new File(l+ "/" + docName +".html"); 
       try{ 
           BufferedWriter w = new BufferedWriter(new FileWriter(output));
             try{ 
               
               output.createNewFile();
               this.location = l; 
               w.write(this.htmlDoc(l).outerHtml());
              
            } 
            finally{ 
                w.close();
            }
        } catch (IOException e) { 
           throw new UIException("Article "+ this.docName + "could not be writen to location: " + l ); 
        }      
    } 
    
    public Document htmlDoc(String l){ 
        //String baseURI = "<a xml:base=\""  + l+ "/" + docName + " \" href=\" "+ l+ "/" + docName  + "\"/>";
        String baseURI = "";
        Document d = Document.createShell(baseURI); 
        d.title("Name"); //set the title of the documet 
        Element b = d.body(); 
        //Element t = b.appendElement("div"); 
        b = b.append("<BR><DIV><span style=\"font-weight:bold\">" + this.title + "</span></P></DIV>")  ; 
        b = b.append("<BR><DIV>" + "Classified as: " + this.classValue +  "</DIV>" ); 
        b = b.appendText("Article written on :" + this.date);
        b = b.append(body.html()); 
        return d;
    }
    
    public TOCEntry createTOCEntry(int id, String fromLocation){ 
        TOCEntry t = new TOCEntry(id, this.location+"/"+this.docName, this.title, this.date, this.classValue, this.htmlFile); 
       
        return t; 
    } 
    
    
    /**
     * Reads dates in long format and converts to yyyy-MM--dd form. 
     *
     * @param  date in MONTH DD, YYYY form  
     * @return  YYYYY-MM-DD  
     */
    public static String dateParse(String d)
    {
      String s = ""; 
      try{ 
          DateFormat inputFormat = new SimpleDateFormat("MMMdd,yyyy");
          Date date1 = inputFormat.parse(d); 
          DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
          String dateString = outputFormat.format(date1); 
          s = dateString; 
     }
     catch (ParseException e){
        s = "Exception : "+e; 
     }
        return s; 
    }

    /**
     * Extracts the FIRST integer from a string
     *
     * @param  line-the original string 
     * @return     the sum of x and y
     */
    public static int getNum(String line)
    {
      line = " " + line+" ";
      String[] split = line.split("(\\d)+", 2); 
      String num = line.substring(split[0].length(), line.length() - split[1].length()); 
     
      return Integer.parseInt(num.trim()); 
    }

    /**
      * returns the title 
     */
    public String getTitle()
    {
        return this.title; 
    }
    
     /**
     * returns the articleDate 
     */
    public String getDate()
    {
        return this.date; 
    }
   
     /**
     * returns the bodyText 
     */
    public String getText()
    {
        return this.body.text(); 
    }
    
    /**
     * Returns the classValue
     */
    public String getClassValue()
    {
        // put your code here
        return this.classValue;
    }
    
    public String getHtmlFile()
    {
        return this.htmlFile; 
    } 

}
