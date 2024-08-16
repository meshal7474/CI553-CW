package clients.cashier;

import catalogue.Basket;
import catalogue.Product;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;

public class CashierController {
    private final CashierModel model;
    private final CashierView view;

    public CashierController(CashierModel model, CashierView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    public void doCheck(String productNum) throws Exception {
        model.doCheck(productNum);
    }

    public void doBuy() {
        model.doBuy();
    }

    public void doBought() {
        model.doBought();
    }

    public void undo() {
        model.undo();
    }

    public void doSale(String promoCode) {
        model.doSale(promoCode);
    }

    public void doTip(double tipAmount) {
        model.doTip(tipAmount);
    }
}
