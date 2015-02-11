
/**
 * This creates a new ARFF Relation document
 * It is used to build the training data. IT hs the methods needed to turn either a file of HTML text of lexis articles 
 * or a tab delimited filed of attributes into an arrff files which can be read into a weka 
 * instances object 
 
 * 
 * @author Rachel Warren
 * @version 12/14/2013 
 */

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.Scanner; 
import java.text.*;
import java.util.*; 
import java.util.Calendar;
import java.text.*;
import java.util.*; 
import weka.core.Utils;


public class Relation
{
    // instance variables - replace the example below with your own
    private final String name; 
    private final String author;
    private final String docName; 

    /**
     * Defualt Constructor for objects of class Relation
     */
    public Relation()
    {
       name = "Defualt" ;
       author = "John Smith" ;
       docName = "defualt.arff" ;
    }
    
    /**
     * Constructor for objects of class Relation given string values as parameters
     * @param n-the name of the relation 
     * @param a-the author 
     * @param docName- the file path of the ARFF doc you want to create MUST be a .arff file 
     */
    
    public Relation(String n, String a, String d) 
    {
       name = n; 
       author = a; 
       docName = d;
    }
    
    /**
     * Writes the attribute header to a arff document 
     * @param date  the date of this article, will be in the  comment 
     * @param atNames   the name of the attributes 
     * @param atTypes   the types of the attributes precondition: |atName| = |atTypes 
     * 
     */
   public void arffHeader(String Date, String description, String[] atNames, String[] atTypes) throws IOException{
      File output = new File(this.docName) ; 
      output.createNewFile(); 
       BufferedWriter w = new BufferedWriter(new FileWriter(output)); 
       
       try{
            String[] lines = {"% Title: ARFF file for " + this.name,
                  "%" + description,
                  "%Created by " + this.author, 
                  " ", 
                  "@RELATION " + this.name,
                  " ", 
                  " %attribute declarations", } ;
               for (String l : lines) {
                        w.newLine();
                        w.write(l);
                        w.flush(); 
                } 
            
              for(int i = 0; i<atNames.length; i++) {
                  w.newLine(); 
                  w.write("@ATTRIBUTE " + atNames[i] + " " + atTypes[i]); 
                  w.flush(); 
                }
                 w.newLine(); 
                 w.write("@data");      
                 w.flush(); 
                
        }
        finally{ 
            w.close(); 
        } 
    }
    
    /**
     * taboDel2arff converts a tab delimited file to the arff file format. 
     * Note: only supports String and Numeric types, use the Numeric to Nominal Filter after applying to convert the numeric vectors. 
     * @param tabName 
     * @param Date
     * @param Description   a comment about what this relation is 
     * @param atNames   the names of the attributes 
     * @param atTypes   the tyes of the attributes 
     * |atName| = |atTypes 
     * 
     * @throws IOException if it cannot find the location to right too 
     */
    public void tabDel2arff(String tabName, String Date, String description, String[] atNames, String[] atTypes) throws IOException{ 
        arffHeader(Date, description, atNames, atTypes) ; 
         BufferedWriter w = new BufferedWriter(new FileWriter(this.docName, true));  
       //Read Document 
          File tn = new File(tabName); 
          Scanner in = new Scanner(tn); 
       String next = in.nextLine();
       try{ 
           while (in.hasNextLine()) {
               String[] values = next.split("\\t", -1);
               String s = "" ;
               for (int i = 0; i < atNames.length; i++){
                   if(atTypes[i].equals("STRING")) { 
                       //String attribute 
                       s = s + Utils.quote(values[i]); 
                    } 
                   else if(atTypes[i].contains("DATE")) { 
                        s = s + "\"" + values[i] + "\""; 
                    }
                   else if (atTypes[i].equals("NUMERIC")) { 
                       s = s + values[i]; 
                    } 
                    else { 
                        s = s + "\"" + values[i] + "\""; 
                    }
                    
                   if (i !=atNames.length-1) {
                           s = s + ", ";
                   } 
                }
               w.newLine(); 
              next = in.nextLine(); 
              w.write(s); 
              w.flush(); 
            }
        }        
       //end try 
        finally { 
            w.close(); 
        } 
    }
            
                  
    /**
     * New Doc creates a new ARFF document with the relation header. The document name will be equal to the 
     * value of the field "docName" 
     * 
     * @param  date String  representation of the date created 
     * @param description   comment describing what this relation does, will appear as a
     * comment in the header
     * 
     * @throws IOException if the file path cannot be found 
     */
    

    public void newDoc(String Date, String description ) throws IOException
    {
       File output = new File(docName); 
       output.createNewFile();  
       BufferedWriter w = new BufferedWriter(new FileWriter(output)); 
       try{ 
           String[] lines = {"% Title: ARFF file for " + this.name,
                  "%" + description,
                  "%Created by " + this.author, 
                  " ", 
                  "@RELATION " + this.name,
                  " ", 
                  " %attribute declarations", 
                  "@ATTRIBUTE lexisnumber  NUMERIC",
                  "@ATTRIBUTE date DATE \"yyyy-MM-dd\" ", 
                  "@ATTRIBUTE articletitle STRING",
                  "@ATTRIBUTE source STRING", 
                  "@ATTRIBUTE wordcount NUMERIC" ,
                  "@ATTRIBUTE text STRING", 
                  "@ATTRIBUTE class {0, 1}", 
                  " ", "%Instances",
                   " ", 
                  "@data", " " }; 
            for (String l : lines) {
               w.newLine();
               w.write(l);
               w.flush(); 
            } 
        }//end try 
        finally{
              w.close(); 
        }
    }
    
    
    
        /**
         * addArticles method parses a document of lexis-nexis querries (in html format) and adds the 
         * articles as instances to this Relation 
         *
         * @param   the file path name, must be in HTML format 
         * @return     void 
         * 
         * @throws IOException if the filepath cannot be found 
         */
    public void addArticles(String lexisdoc) throws IOException{
     BufferedWriter w = new BufferedWriter(new FileWriter(this.docName, true));  
       //Read Html Document 
     File lexis = new File(lexisdoc); 
     Scanner in = new Scanner(lexis); 
     String next = in.nextLine();
     try{
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
              //LexisInstance a = new LexisInstance(articleHTML); 
              Article a = new Article(articleHTML); 
              w.write(a.toARFFString());
              w.newLine(); 
              w.flush();
            } 
            else {
               articleHTML = articleHTML + next ; 
            }      
        }
        next = in.nextLine();   
       } 
    }//end try block
    finally{
         w.close(); 
        }
    }//end method 
    
        /**
         * LexisInstances method parses a document of lexis-nexis querries (in html format) and returns them as an arrayList of the LexisInstance types 
         *
         * @param   the file path name, must be in HTML format 
         * @return     an array of the Lexis Instance objects 
         * 
         * @throws IOException if the filepath cannot be found
         */
    public ArrayList<String> LexisInstances(String lexisdoc) throws IOException
    {
         ArrayList<String> textList = new ArrayList<String>();  
           //Read Html Document 
         File lexis = new File(lexisdoc); 
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
                  //LexisInstance a = new LexisInstance(articleHTML); 
                  textList.add(articleHTML); 
                } 
                else {
                   articleHTML = articleHTML + next ; 
                }      
            }
            next = in.nextLine();   
           } 
           return textList; 
    }//end me
    
    /**
     * Adds instances to this relation of the articles in the lexisdoc which it takes as its parameter.
     * 
     * @param lexisdox  a document of html text of the lexisnexis output 
     * 
     * @throws IOException if the filepath cannot be found
     */
     public int addInstance(String lexisdoc) throws IOException
    {
     int i = 0 ;
     BufferedWriter w = new BufferedWriter(new FileWriter(this.docName, true));  
       //Read Html Document 
     File lexis = new File(lexisdoc); 
     Scanner in = new Scanner(lexis); 
     String next = in.nextLine();
     try{
     while (in.hasNextLine())  {
        boolean inDoc = false;  
        if (next.contains("<DOCFULL>")){
             inDoc = true;
           } 
            String articleHTML = " ";
        while(inDoc) {
           next= in.nextLine(); 
           if (next.contains("DOCFULL")){
              inDoc = !inDoc;    
              Article a = new Article(articleHTML); 
              w.write(a.toARFFString());
              i++; 
              w.newLine(); 
              w.flush(); 
            } 
            else {
               articleHTML = articleHTML + next ; 
            }      
        }
        next = in.nextLine();   
       } 
    }//end try block
    finally{
         w.close(); 
        }
        return i ;
    }//end method 
    
    
}
