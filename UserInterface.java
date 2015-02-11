/**
 * Includes the main method which runs the user interface. Makes a form where the user can enter the 
 * name of the directory where they want to store the classified articles and the file name of the
 * html file they want to classify. 
 * 
 * @author Rachel Warren  
 * @version 12-14-2012e htm
 */ 
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*; 
import java.util.*; 
import java.text.SimpleDateFormat;
   import java.awt.GridLayout;
import javax.swing.*;

public class UserInterface
{
 
    private int year;
    private String rootDirectory; 
    private String htmlPath;  
     
  /**
   * defualt constructor Constructor
   */
  public UserInterface(){  
        int year = 2008; 
        String rootDirectory = "";  
        String htmlPath = "";  
   }
   /**
    * @param message    the message that appears at the top of the form  
    * 
    * @see the form where the user makes the directory 
    */
   public void makeForm(String message){ 
        String[] items = {"2008", "2012: not supported right now"};
        JComboBox combo = new JComboBox(items);
        JTextField rd = new JTextField("");
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel(message)); 
        panel.add(new JLabel("hit cancel to exit the program")); 
        panel.add(new JLabel("Which year is this model for: ")); 
        panel.add(combo);
        panel.add(new JLabel("Enter the directory where you would like to store the articles:"));
        panel.add(new JLabel("For example:  C:/Users/rwarren/Documents/lexisProject01/  ")); 
        panel.add(rd);
        panel.add(new JLabel(" Which variable does this project model"));
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if(combo.getSelectedItem() == "2012"){ 
                year = 2012; 
            }
             else { 
                year = 2008; 
             } 
        
            rootDirectory = rd.getText(); 
        
        } else {
            System.out.println("Exiting.");
            System.exit(0); 
        }
    }
    /**
     * Recrusively prints a form for the user to enter in an article to add to the coding project 
     * 
     * @param message that will appear in the top of the form 
     * 
     * @see the form 
     */
     public void articleForm(String message){ 
         
        JTextField rd = new JTextField("");
        JTextField hp = new JTextField("");
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel(message)); 
        panel.add(new JLabel("hit cancel to exit the program")); 
        panel.add(new JLabel("")); 
        panel.add(new JLabel("For example:  C:/Users/rwarren/Documents/lexisProject01/  ")); 
        panel.add(new JLabel("Enter the file location : "));  
        panel.add(hp);
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            htmlPath = hp.getText();   
        } else {
            System.out.println("Exiting.");
            System.exit(0); 
        }
    }
   
    /**
     * A main method that runs the user interface. 
     * Bringing up a form fo the user to enter the data. Allows users to add several articles to one 
     * classification project or just one. 
     */
    public static void main(String[] args){ 
        UserInterface ui = new UserInterface(); 
        boolean runAgain = true; 
        CodingProject project = null; 
        String m = "Create new classification project. Fill in the following form and click 'OK' ";
         JFrame frame = new JFrame("Lexis Classification Project" );
        while(runAgain){ 
             ui.makeForm(m); 
             try{ 
                   //project = new CodingProject(ui.rootDirectory, ui.year, ui.factChecking); 
                   System.out.println("Creating new coding project ...");
                   project = new CodingProject(ui.rootDirectory, ui.year);
                   runAgain = false; 
                } 
               catch (ConvertException e){  
                   String s = " Check that installation is correct and rerun program. ";
                   JOptionPane.showMessageDialog(frame,
                        e.getMessage() + s,
                                "ConvertException",
                                    JOptionPane.ERROR_MESSAGE);
                }
               catch (UIException e){ 
                   JOptionPane.showMessageDialog(frame, e.getMessage(), "Invalid Input",
                                         JOptionPane.ERROR_MESSAGE);
                    m = "Try re-entering information"; 
                } 
             
                boolean addFiles = true; 
                runAgain = ui.addArticles(addFiles, "Add Articles", frame, project); 
               

                 Object[] options = {"Yes",
                                     "No",};
                 int n = JOptionPane.showOptionDialog(frame,
                            "Would you like create another classification project?" ,
                             "",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                  if(n == 0) {
                       runAgain = true; 
                    } 

            } //end running loop 
            System.exit(0); 
            
        }//end main method 
     
     /**
      * Addarticles adds html documetns to the current coding project. 
      * @param AddFiles 
      * @param m 
      * @param JFrame frame 
      * @param CodingProject project 
      */   

    public  boolean addArticles( boolean addFiles, String m, JFrame frame, CodingProject project ){ 
        if(addFiles){ 
              Object[] options = {"Yes",
                                 "No",};
                        int n = JOptionPane.showOptionDialog(frame,
                        "Would you like create to add another file to this project?" ,
                         "",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
              if(n == 0) {
                    try{ 
                       this.articleForm(m);
                           System.out.println("Writing Articles");
                           String s = (project.classifyArticles(this.htmlPath));                               
                           JOptionPane.showMessageDialog(frame, s, "Articles succesfully classified! ",
                                        JOptionPane.PLAIN_MESSAGE); 
                    }
                    catch (UIException e){ 
                       JOptionPane.showMessageDialog(frame, e.getMessage(), 
                       "Invalid Input", JOptionPane.ERROR_MESSAGE); 
                       m = "Articles could not be parsed, please re-enter classification info "; 
                      
                     } 
                     catch (NullPointerException e ){ 
                         JOptionPane.showMessageDialog(frame, e.getMessage(), 
                       "Null Pointer Exception", JOptionPane.ERROR_MESSAGE); 
                       m = "Articles could not be pased, please re-enter classification info "; 
                       
                    }
                   addFiles = true; 
                   return this.addArticles(addFiles, m, frame, project); 
                } 
                else{ 
                     try{
                        String mTOC = project.returnTOC(false, true); //does not sort the list, include sthe failed article
                        JOptionPane.showMessageDialog(frame,
                                    mTOC,"Table of Contents Written ",
                                                JOptionPane.PLAIN_MESSAGE); 
                        } 
                     catch (Exception e){ 
                         JOptionPane.showMessageDialog(frame, e.getMessage(), 
                         "Write failed", JOptionPane.ERROR_MESSAGE); 
                      }          
                     return false; 
                  
                    }
                }
         else{
             return false; 
            }
        }
           
}  
