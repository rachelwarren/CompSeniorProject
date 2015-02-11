
/**
 * Create data builds the data sets for the 2008 data from the arff files.
 * The output of this file has more attributes than it needs because it will 
 * include the string of title and and date used to match the articles and both the class values. 
 * Use intstance utils 
 * 
 * @author Rachel 
 * @version Version 1 11.15.2013
 */

//Import statements 
 import weka.core.FastVector; 
 import weka.core.Instances;
 import weka.core.Instance;
 import weka.core.Attribute; 
 import java.io.BufferedReader;
 import java.io.FileReader;
 import java.io.*; 
 import java.util.*; 
 import weka.filters.unsupervised.attribute.NumericToNominal; 
 import weka.filters.Filter;
 import weka.filters.unsupervised.attribute.AddID;
 import java.util.Arrays;
 
public class LabeledData
{
    // instance variables - replace the example below with your own
    private static String TODAY = "10-22-2012"; 
    private static String LEXIS_ARFF08 = "files/2008/lexis08.arff"; //arff file with lexis data from 08
    private static String[] HTML_FILES08 = {"files/2008/2008-1.HTML", "files/2008/2008-2.HTML"} ;
    private static String ME = "Rachel Warren"; 
    private static String CODING_ARFF08 = "files/2008/codingData08.arff"; //the name of the arff file to crate, human coded data for training: title, date, result of fact check var.
    //codingData vs. coding is the most recent
    private static String CODING_TXT08 = "files/2008/codingData08.txt" ; //numeric fact checking variable (tab delimeted file). 
    private static String TITLES_TXT08 = "files/2008/titles2008.txt"; //for the title match, is simply 
    private static String TITLES_ARFF08 = "files/2008/titles2008.arff"; 

    private static String LEXIS_ARFF12 = "files/2012/lexis12.arff"; //arff file with lexis data from 08
    private static String[] HTML_FILES12 = {"files/2012/2012-1.HTML", "files/2008/2008-2.HTML", "files/2012/2012-3.HTML", 
                                             "files/2012/2012-4.HTML", "files/2012/2012-5.HTML"                           } ;
    private static String CODING_ARFF12 = "files/2012/codingData12.arff"; //codingData vs. coding is the most recent
    private static String CODING_TXT12 = "files/2012/codingData12.txt" ; //numeric fact checking variable 
    private static String TITLES_TXT12 = "files/2012/titles2012.txt";
    private static String TITLES_ARFF12 = "files/2012/titles2012.arff"; 
   
    
    //These files already exists upon initalization of the create data object 
    private String[] html_files; //an array of the names of all the html files to be parsed 
      private String coding_txt; //tab delimeted file with the names of the 
    private String titles_txt; 
    
    //these are the names of the arff files that will be created. 
    private String lexis_arff; 
    private String coding_arff;
    private String titles_arff;  
    
    // private static String CODING_TXT08 = "files/2008/article_data_20082.txt"; //string fact checking variable 
    /**
     * Constructor for objects of class LabeledData
      */
     public LabeledData() throws Exception{
 
          throw new ConvertException("Please include file names for conversion") ;
        }
        
        /**
         * Constructor for class LabeledData
         * @param year articles were written in  
         */
    public LabeledData(int year) throws Exception 
    {
       if (year == 2012) { 
             lexis_arff = LEXIS_ARFF12; 
             html_files = HTML_FILES12 ;
             coding_arff = CODING_ARFF12;
             coding_txt = CODING_TXT12; 
             titles_txt = TITLES_TXT12; 
             titles_arff = TITLES_ARFF12;    
             
        } 
        else if (year == 2008) { 
            lexis_arff = LEXIS_ARFF08; 
            html_files = HTML_FILES08;
            coding_arff = CODING_ARFF08;
            coding_txt = CODING_TXT08; 
            titles_txt = TITLES_TXT08; 
            titles_arff = TITLES_ARFF08; 
        } 
        else {
            throw new ConvertException("Labeled Class: input a different year to crate data constructor") ;
        }

    }
   
    /**
     * Build Arff Files 
     *@param str  a description of this file 
     */
    public void buildArff(String str) throws Exception
    {
        //the titles to merge in in order to remove the new articles from lexis nexis. 
        Relation titles = new Relation("Titles", ME , titles_arff) ;
        String[] names = {"title" , "date", "was_coded"} ; 
        String[] types = {"STRING", "DATE \"yyyy-MM-dd\"", "NUMERIC"} ;
        titles.tabDel2arff(titles_txt, TODAY, str , names, types ) ;
          //lesix article data 
        Relation lexis = new Relation("ArticleData",ME, lexis_arff);
        lexis.newDoc(TODAY, "This is the initial data from the lexis nexis articles "); 
         //add all HTML files 
         for (String s : html_files) {
              lexis.addArticles(s); 
            }
            
         //create arff file for meta data from human coding  
         Relation code = new Relation("CodingData", ME , coding_arff) ;
         String[] names2 = {"title" , "date", "factchecking"} ; 
         String[] types2 = {"STRING", "DATE \"yyyy-MM-dd\"", "NUMERIC"} ;
         code.tabDel2arff(coding_txt, TODAY, 
              "arff file of coding, contains only the article name and the date of the article", names2, types2 ) ;   
    }
    
    /**
     * Rebuilds the arff file with the titles    
     */
    public void buildTitles() throws Exception
    {
         //the titles to merge in in order to remove the new articles from lexis nexis. 
        Relation titles = new Relation("Titles", ME , titles_arff) ;
        String[] names = {"title" , "date", "was_coded"} ; 
        String[] types = {"STRING", "DATE \"yyyy-MM-dd\"", "NUMERIC"} ;
        titles.tabDel2arff(titles_txt, TODAY, "titles of articles which were reviewed by human coders" , names, types ) ;
    }
    
    
       /**
     * Rebuilds the arff file with the article data    
     */
    public void buildArticleData() throws Exception
    {
         //the titles to merge in in order to remove the new articles from lexis nexis. 
        Relation lexis = new Relation("ArticleData",ME, lexis_arff);
        lexis.newDoc(TODAY, "Lexis nexis article data parsed from html files: "+ Arrays.toString(html_files)); 
         //add all HTML files 
         for (String s : html_files) {
              lexis.addArticles(s); 
            }
    }

    /**
     * build or rebuilds the arff data from the files
     * @throws IOEception 
     */
    public void buildCodingData() throws Exception
    {
         Relation code = new Relation("CodingData", ME , coding_arff) ;
         String[] names2 = {"title" , "date", "factchecking"} ; 
         String[] types2 = {"STRING", "DATE \"yyyy-MM-dd\"", "NUMERIC"} ;
         code.tabDel2arff(coding_txt, TODAY, 
              "arff file of coding, contains only the article name and the date of the article", names2, types2 ) ;  
    }


    /**
     * A method that merges in the titles that we actually looked at an coded and removes the things that we need
     *Precodintion, the match variable is already se up (titles, lower case concatenated with date) and that that is the 
     *last variablein the data set, data 
     * @param  titlesARFF arff files with the titles (lower case), date, and if it was coded. 
     * @return     the merged data 
     * 
     * @throws IOException 
     * @throws weka exception 
     */
    public static Instances deleteUncoded(String titlesARFF, Instances data, int[] match) throws Exception
    {
        //preprocess the title data:  
         BufferedReader reader = new BufferedReader( new FileReader(titlesARFF));
        try{
            Instances titles = new Instances(reader);
             
            for (int i = 0; i< titles.numInstances(); i++) { 
                 String s = titles.instance(i).stringValue(0).trim(); 
                 if (s.length() <1) {
                     titles.instance(i).setValue(0, "No title found"); 
                    } 
                 else{ 
                     if (s.charAt(1) == '\"')  {
                         s = s.substring(1,s.length());
                        }
                     if (s.charAt(s.length()-1) == '\"') {
                         s = s.substring(0, s.length()-1);
                        }
                 titles.instance(i).setValue(0, s.trim()); 
                  }     
                }    
            //Sort and remove duplicates:     
            int[] so2 = {1, 0}; 
            titles = InstanceUtils.removeDuplicates(titles, so2 );
            int[] attributesB = {2}; //merge in the last attribute 
            int[] matchB = {0,1}; 
            data = InstanceUtils.m2OneMerge(data, titles, match, matchB, attributesB , "0" ); 
              for (int n = data.numInstances()-1; n >= 0; n--) {
                if ((data.instance(n).value(data.numAttributes()-1) !=1 )) {
                    data.delete(n);
                }
            }     
            data.deleteAttributeAt(data.numAttributes()-1); 
            data.deleteAttributeAt(data.numAttributes()-1);  
            return data; 
            }
        finally{
            reader.close();
        }    
    }
   /** 
    * This method builds the 2008 data from the arff files and reutrns the Instances. 
    * If the ARFF files have not been created yet, it calls a method which builds them from the paths of the txt files given in the constructor. 
    * 
    * Merges coding data with lexis data
    * Removes uncoded instances (by merging the titles of all the coded artilces) 
    * Deletes duplicate attributes 
    * Sorts 
    * 
    * @return data ready for analysis. 
    * @throws Weka exception if there is an error in the data and classifier 
    */
  
   public Instances buildData() throws Exception {
       File t = new File(titles_arff); 
         File c = new File(coding_arff); 
         File la = new File(lexis_arff); 
         if(!t.isFile()){ 
            this.buildTitles();
            } 
         if(!c.isFile()){ 
             this.buildCodingData(); 
            } 
         if(!la.isFile()){ 
             this.buildArticleData(); 
            }
     //read in the arff files 
     BufferedReader reader = new BufferedReader( new FileReader(this.LEXIS_ARFF08));
      //MAKE READER 2 
     BufferedReader reader2 = new BufferedReader( new FileReader(this.CODING_ARFF08));  
     try{
        Instances data = new Instances(reader); 
        data.deleteAttributeAt(data.numAttributes()-1); //delete the class variable from the lexis data 
        Instances codingData = new Instances(reader2);   
        codingData.sort(1); //sort coding data by date 
        //remove duplicate instances from data 
        int[] sortOn = {1, 2} ;
        data = InstanceUtils.removeDuplicates(data, sortOn); 
        int[] so2 = {1, 0}; 
        codingData = InstanceUtils.removeDuplicates(codingData, so2 );
         //creates the new variable to match on
        int atNumber = data.numAttributes(); //number of attribute in lexis data
        FastVector a = new FastVector(); 
        Attribute match = new Attribute("match", (FastVector) null );
        data.insertAttributeAt( match, atNumber); 
        for (int i = 0; i< data.numInstances(); i++)   {
          data.instance(i).setValue(atNumber, data.instance(i).stringValue(2).toLowerCase().trim());        
        }
        //get rid of leading and trailing quotes for title attribute 
        for (int i = 0; i< codingData.numInstances(); i++) { 
            String s = codingData.instance(i).stringValue(0); 
            if (s.charAt(0) == '\"')  {
                codingData.instance(i).setValue(0, s.substring(1, s.length()-1).trim());      
            } 
        } 
        //This is the point when I will merge in the coding data 
         int[] attributesB = {2} ; //the attributes of the coding data to keep 
        ///I think this should be deleted.... this.buildArff("arff file of titles, contains only the article name and the date of the article"); 
         int[] amatch = {atNumber, 1} ;
         int [] bmatch = {0, 1}; 
         //Instances i = new Instances(TITLES_ARFF08);
         Instances data2 = LabeledData.deleteUncoded(titles_arff, data, amatch);        
         data2 = InstanceUtils.m2OneMerge(data, codingData, amatch, bmatch, attributesB , "0" );
         FastVector f = new FastVector(); 
         f.addElement("0"); 
         f.addElement("1"); 
         Attribute ai = new Attribute("_isFactChecking", f); 
         data2.insertAttributeAt(ai, data2.numAttributes()-1); 
         int l = data2.numAttributes();     
         for(int i = 0; i< data2.numInstances(); i++){
             if( data2.instance(i).value(data2.numAttributes()-3) > 0.0 ) {
                 data2.instance(i).setValue(l-2, 1); 
            }
            else {
                data2.instance(i).setValue(l-2,0);
            }
        }   
        data2.deleteAttributeAt(l-3); 
        //AddID aid = new  AddID(); 
        //aid.setInputFormat(data2); 
        //data2 = Filter.useFilter(data2, aid);  
       
         return data2; 
        } //end try 
    finally{
             reader.close();
          reader2.close();
        }   
    } //end method 


} //end of class 