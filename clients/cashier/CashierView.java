package clients.cashier;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * View of the model
 */
public class CashierView implements Observer {
    private static final int H = 300;       
    private static final int W = 400;       

    private static final String CHECK  = "Check";
    private static final String BUY    = "Buy";
    private static final String BOUGHT = "Bought";
    private static final String UNDO   = "Undo";  
    private static final String SALE   = "SALE";  
    private static final String TIP    = "Tip";   

    private final JLabel      theAction  = new JLabel();
    private final JTextField  theInput   = new JTextField();
    private final JTextArea   theOutput  = new JTextArea();
    private final JScrollPane theSP      = new JScrollPane();
    private final JButton     theBtCheck = new JButton(CHECK);
    private final JButton     theBtBuy   = new JButton(BUY);
    private final JButton     theBtBought= new JButton(BOUGHT);
    private final JButton     theBtUndo  = new JButton(UNDO);  
    private final JButton     theBtSale  = new JButton(SALE);  
    private final JButton     theBtTip   = new JButton(TIP);   

    private StockReadWriter theStock     = null;
    private OrderProcessing theOrder     = null;
    private CashierController cont       = null;

    /**
     * Construct the view
     * @param rpc   Window in which to construct
     * @param mf    Factory to deliver order and stock objects
     * @param x     x-coordinate of position of window on screen
     * @param y     y-coordinate of position of window on screen
     */
    public CashierView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        try {
            theStock = mf.makeStockReadWriter();
            theOrder = mf.makeOrderProcessing();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing view: " + e.getMessage(), "Initialization Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Print stack trace for debugging
            return; // Exiting constructor to prevent further execution
        }

        Container cp         = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(null);
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);

        // Set the background color to dark grey
        cp.setBackground(Color.DARK_GRAY);

        Font f = new Font("Monospaced", Font.PLAIN, 12);

        theBtCheck.setBounds(16, 25 + 60 * 0, 80, 40);
        theBtCheck.addActionListener(e -> {
			try {
				cont.doCheck(theInput.getText());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        cp.add(theBtCheck);
        theBtCheck.setOpaque(true);
        theBtCheck.setBackground(new Color(0, 0, 139)); // Dark blue
        theBtCheck.setForeground(Color.WHITE); // Set text color to white
        theBtCheck.setBorderPainted(false);

        theBtBuy.setBounds(16, 25 + 60 * 1, 80, 40);
        theBtBuy.addActionListener(e -> cont.doBuy());
        cp.add(theBtBuy);
        theBtBuy.setOpaque(true);
        theBtBuy.setBackground(new Color(0, 0, 139)); // Dark blue
        theBtBuy.setForeground(Color.WHITE); // Set text color to white
        theBtBuy.setBorderPainted(false);

        theBtSale.setBounds(16, 25 + 60 * 2, 80, 40); // Position SALE button
        theBtSale.addActionListener(e -> {
            String promoCode = JOptionPane.showInputDialog("Enter promo code:");
            if (promoCode != null) {
                cont.doSale(promoCode);
            }
        });
        cp.add(theBtSale);
        theBtSale.setOpaque(true);
        theBtSale.setBackground(new Color(0, 128, 0)); // Green
        theBtSale.setForeground(Color.WHITE); // Set text color to white
        theBtSale.setBorderPainted(false);

        theBtTip.setBounds(16, 25 + 60 * 3, 80, 40); // Position TIP button
        theBtTip.addActionListener(e -> {
            String tipInput = JOptionPane.showInputDialog("Enter tip amount:");
            try {
                double tipAmount = Double.parseDouble(tipInput);
                cont.doTip(tipAmount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid tip amount.");
            }
        });
        cp.add(theBtTip);
        theBtTip.setOpaque(true);
        theBtTip.setBackground(new Color(0, 128, 128)); // Teal
        theBtTip.setForeground(Color.WHITE); // Set text color to white
        theBtTip.setBorderPainted(false);

        theBtBought.setBounds(16, 25 + 60 * 4, 80, 40);
        theBtBought.addActionListener(e -> cont.doBought());
        cp.add(theBtBought);
        theBtBought.setOpaque(true);
        theBtBought.setBackground(new Color(0, 0, 139)); // Dark blue
        theBtBought.setForeground(Color.WHITE); // Set text color to white
        theBtBought.setBorderPainted(false);

        theBtUndo.setBounds(16, 25 + 60 * 5, 80, 40);  
        theBtUndo.addActionListener(e -> cont.undo());
        cp.add(theBtUndo);
        theBtUndo.setOpaque(true);
        theBtUndo.setBackground(Color.RED);             
        theBtUndo.setForeground(Color.WHITE); // Set text color to white
        theBtUndo.setBorderPainted(false);

        theAction.setBounds(110, 25 , 270, 20 );
        theAction.setText("");
        theAction.setForeground(Color.WHITE); // Set text color to white
        cp.add(theAction);

        theInput.setBounds(110, 50, 270, 40 );
        theInput.setText("");
        cp.add(theInput);

        theSP.setBounds(110, 100, 270, 160 );
        theOutput.setText("");
        theOutput.setFont(f);
        cp.add(theSP);
        theSP.getViewport().add(theOutput);
        rootWindow.setVisible(true);
        theInput.requestFocus();
    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     * @param c   The controller
     */
    public void setController(CashierController c) {
        cont = c;
    }

    /**
     * Update the view
     * @param modelC   The observed model
     * @param arg      Specific args
     */
    @Override
    public void update(Observable modelC, Object arg) {
        CashierModel model  = (CashierModel) modelC;
        String      message = (String) arg;
        theAction.setText(message);
        
        Basket basket = model.getBasket();
        if (basket != null) {
            theOutput.setText(basket.getDetails());
        } else {
            theOutput.setText("No items in basket");
        }

        theInput.requestFocus();
    }
}
