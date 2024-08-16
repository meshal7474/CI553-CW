package clients.backDoor;

import middle.MiddleFactory;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view.
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */

public class BackDoorView implements Observer
{
  private static final String RESTOCK  = "Add";
  private static final String CLEAR    = "Clear";
  private static final String QUERY    = "Query";
 
  private static final int H = 300;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  private final JLabel      theAction  = new JLabel();
  private final JTextField  theInput   = new JTextField();
  private final JTextField  theInputNo = new JTextField();
  private final JTextArea   theOutput  = new JTextArea();
  private final JScrollPane theSP      = new JScrollPane();
  private final JButton     theBtClear = new JButton(CLEAR);
  private final JButton     theBtRStock = new JButton(RESTOCK);
  private final JButton     theBtQuery = new JButton(QUERY);
  
  private StockReadWriter theStock     = null;
  private BackDoorController cont = null;

  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-cordinate of position of window on screen 
   * @param y     y-cordinate of position of window on screen  
   */
  public BackDoorView(RootPaneContainer rpc, MiddleFactory mf, int x, int y)
  {
    try                                             // 
    {      
      theStock = mf.makeStockReadWriter();          // Database access
    } catch (Exception e)
    {
      System.out.println("Exception: " + e.getMessage());
    }
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize(W, H);                       // Size of Window
    rootWindow.setLocation(x, y);

    // Set the background color to dark grey for the content pane
    cp.setBackground(Color.DARK_GRAY);
    
    Font f = new Font("Monospaced", Font.PLAIN, 12);  // Font f is

    theBtQuery.setBounds(16, 25 + 60 * 0, 80, 40);    // Query button 
    theBtQuery.addActionListener(                   // Call back code
      e -> cont.doQuery(theInput.getText()));
    theBtQuery.setBackground(new Color(0, 0, 139)); // Dark blue
    theBtQuery.setForeground(Color.WHITE);          // Set text color to white
    theBtQuery.setBorderPainted(false);
    cp.add(theBtQuery);                           // Add to canvas

    theBtRStock.setBounds(16, 25 + 60 * 1, 80, 40);   // Restock Button
    theBtRStock.addActionListener(                  // Call back code
      e -> cont.doRStock(theInput.getText(), theInputNo.getText()));
    theBtRStock.setBackground(new Color(0, 0, 139)); // Dark blue
    theBtRStock.setForeground(Color.WHITE);          // Set text color to white
    theBtRStock.setBorderPainted(false);
    cp.add(theBtRStock);                          // Add to canvas

    theBtClear.setBounds(16, 25 + 60 * 2, 80, 40);    // Clear button 
    theBtClear.addActionListener(                   // Call back code
      e -> cont.doClear());
    theBtClear.setBackground(new Color(0, 0, 139)); // Dark blue
    theBtClear.setForeground(Color.WHITE);          // Set text color to white
    theBtClear.setBorderPainted(false);
    cp.add(theBtClear);                           // Add to canvas

    theAction.setBounds(110, 25, 270, 20);           // Message area
    theAction.setText("");                           // Blank
    theAction.setForeground(Color.WHITE);            // Set text color to white
    cp.add(theAction);                              // Add to canvas

    theInput.setBounds(110, 50, 120, 40);            // Input Area
    theInput.setText("");                            // Blank
    theInput.setBackground(Color.WHITE);             // Set background color to white
    theInput.setForeground(Color.BLACK);             // Set text color to black
    cp.add(theInput);                               // Add to canvas
    
    theInputNo.setBounds(260, 50, 120, 40);          // Input Area
    theInputNo.setText("0");                         // 0
    theInputNo.setBackground(Color.WHITE);           // Set background color to white
    theInputNo.setForeground(Color.BLACK);           // Set text color to black
    cp.add(theInputNo);                             // Add to canvas

    theSP.setBounds(110, 100, 270, 160);             // Scrolling pane
    theOutput.setText("");                           // Blank
    theOutput.setFont(f);                            // Uses font  
    theOutput.setBackground(Color.WHITE);            // Set background color to white
    theOutput.setForeground(Color.BLACK);            // Set text color to black
    cp.add(theSP);                                  // Add to canvas
    theSP.getViewport().add(theOutput);              // In TextArea
    
    rootWindow.setVisible(true);                    // Make visible
    theInput.requestFocus();                        // Focus is here
  }
  
  public void setController(BackDoorController c)
  {
    cont = c;
  }

  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update(Observable modelC, Object arg)
  {
    BackDoorModel model  = (BackDoorModel) modelC;
    String        message = (String) arg;
    theAction.setText(message);
    
    theOutput.setText(model.getBasket().getDetails());
    theInput.requestFocus();
  }

}
