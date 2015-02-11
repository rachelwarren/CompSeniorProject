
PROJECT TITLE: Comp360 Final
AUTHORS: Rachel Warren 
PURPOSE OF PROJECT: To classify lexisNexis Newspaper articles 
The project serves to primary functions: 
1.	It prints out evaluation metrics for two different models. 
2.	It runs a GUI that builds and trains a classifier from existing files stored in the “files” directory. Then, it creates two directories, one for “relevant articles” and one for “not relevant articles”. Then given an html document of articles, it will predict whether the articles are relevant or not according to classify and print them (in nice HTML code) to the corresponding directories. It also prints an article table of contents, which gives the title of each article, its predicted class, and the precise location it is now printed in. That table of contents is saved in the root directory and call “TableOfContents.txt.” 

HOW TO START THIS PROJECT: Start from the command line through one of four classes: 
ONE: to print out the results of the different models (made using different preprocessing steps in particular different stemming, stop word removal, and custom adjustments) run the printFinalModels.java class. 
TWO: to print the tables of the evaluations for the different threshold 
Optimizations (how the classifier preforms when optimizing for recall,  run the ThresholdEvaluations tests run ThresholdEvaluations.Java  
THEE: to use the GUI to classify new newsWires run the UserInterface class. 
	The GUI takes the path of a file with html text of lexis-nexis articles, it then classifies those articles based on the training data stored with the app and prints them as a text document in one of two different directories based on how the article is classified. 
FOUR: to simply classify all of the newsWires available in the files folder of this project run the classify newswires.java class. 

Explanation of the way this code works: 

*Article: An article object represents on article as a single line of an instance, as set of fields to write, at HTML code. 
The primary constructor it uses takes a string of HTML text, which it parses and then saves as lexis
number, title, source, body and text fields. 
*Relation: To build the training data the article class is called by the relation class which takes  the methods to parse
and entire file of html text into discrete article chunk as well as write that text as instances to a weka arff file. 
*Labeled Data: The labeled data class creates an Instances object with the coding data and the article data merges (using 
the Relation class. 
*Instance Utils: Methods to clean the labeled data class to get it ready for analysis 
*LexisClassifier: constructs the model given training data and a function (an object which implements the Preproccessor interface) and trains a classifier. 
Has methods to classify new instances and run evaluation tests on the classifier. 
*TestUtils: static methods for printing Evaluations 
*ResultsWriter: methods for printing different evaluation tables, users PrintUtils class and is called by 
the ThresholdEvaluations and printFinalModels classes. 
 
GUI Component
*Article Class: represents the articles, has a constructor which takes not only the HTML but also a classifier and sets the value of the class field of the article object as 0 or 1 depending on how it is classified. This class also has the methods which write the article into neat HTML code to a directory provided by the coding project class. 
*TOC Entry: a single entry in the table of contents, implements comparable so that it can be sorted by title 
*TOC table of contents object, includes an array list of TOCEntry objects and a location to write to essentially 
*CodingProject An object representing one classification task, has the methods to construct the directories where the classified documents will be printed then a method which allows articles to be added. It also stores a TOC object that is updated when the articles are added and can be sorted and printed from this class. 
* UserInterface the graphical component which creates and adds articles to a CodingProject object. 


