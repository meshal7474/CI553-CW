package clients.cashier;

import catalogue.*;
import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;

/**
 * The standalone Cashier Client.
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
public class CashierClient {
    public static void main(String args[]) {
        String stockURL = args.length < 1
                ? Names.STOCK_RW
                : args[0];
        String orderURL = args.length < 2
                ? Names.ORDER
                : args[1];

        RemoteMiddleFactory mrf = new RemoteMiddleFactory();
        mrf.setStockRWInfo(stockURL);
        mrf.setOrderInfo(orderURL);
        displayGUI(mrf);
    }

    private static void displayGUI(MiddleFactory mf) {
        JFrame window = new JFrame();

        window.setTitle("Cashier Client (MVC RMI)");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CashierModel model = new CashierModel(mf);
        CashierView view = new CashierView(window, mf, 0, 0);
        CashierController cont = new CashierController(model, view);
        view.setController(cont);

        model.addObserver(view);
        window.setVisible(true);
        model.askForUpdate();
    }
}
