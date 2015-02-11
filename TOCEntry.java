
/**
 * Toc Entry is an object representing one line (one article) in the table of contents. 
 * 
 * @author Rachel Warren 
 * @version 12/14/2013
 * 
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
import java.text.SimpleDateFormat;
public class TOCEntry  implements Comparable<TOCEntry> {
    
    private int id; 
    private String articleFile; 
    private String articleTitle;
    private String articleDate; 
    private String classStr;
    private String htmlFile; 
    private String dateAdded; 
    /**
     * Constructor for objects of class TOCEntry
     */
    public TOCEntry()
    {
        id = 0; 
        articleFile = "?"; 
        articleTitle = "?"; 
        articleDate = "?"; 
        classStr = "?"; //missing value in Weka 
        htmlFile = ""; 
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(today);
        dateAdded = dateString; 
    }
    
    /**
     * Constructor for objects of class TOCEntry
     * @param id 
     * @param af    the file the article is store in 
     * @param t  title of the article he 
     * @param cs    the class the article was assigned to 
     * @param h the path and file name of the html file 
     */
    
    public TOCEntry(int i, String aF, String t, String d, String cs, String h)
    {
        id = i; 
        articleFile = aF; 
        articleTitle = t;
        articleDate = d; 
        classStr = cs;
        htmlFile = h; 
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(today);
        dateAdded = dateString; 
    }
    /**
     * Tab-delimitted representation of this entry 
     * @return line 
     */
    public String toString(){ 
        String[] as = {articleFile, articleTitle, articleDate, classStr, htmlFile, dateAdded} ;
        String line = id + " "; 
        for (String s : as) { 
            line = line + "\t" + s; 
        } 
        return line; 
    } 
   
    /**
     * Used to srot such by article title 
     * @override
     */
    public int compareTo(TOCEntry other){
       // int cmp = Double.compare(other.articleTitle, this.articleTitle);
        int cmp = other.articleTitle.compareTo(this.articleTitle); 
         if (cmp != 0){ 
             return cmp;
            }
         else{ 
            cmp = other.id - this.id; 
                return cmp; 
            }    
        }      
    }

