package clients.cashier;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.Observable;
import java.util.Stack;

/**
 * Implements the Model of the cashier client
 * @author Mike Smith University of Brighton
 * @version 1.0
 */
public class CashierModel extends Observable {
    private enum State { process, checked }

    private State theState = State.process;
    private Product theProduct = null;
    private Basket theBasket = null;

    private String pn = "";

    private StockReadWriter theStock = null;
    private OrderProcessing theOrder = null;

    private Stack<CashierModel> undoStack = new Stack<>();

    
    private Product lastAddedProduct = null;

    public CashierModel(MiddleFactory mf) {
        try {
            theStock = mf.makeStockReadWriter();
            theOrder = mf.makeOrderProcessing();
        } catch (Exception e) {
            DEBUG.error("CashierModel.constructor\n%s", e.getMessage());
        }
        theState = State.process;
    }

    public Basket getBasket() {
        return theBasket;
    }

    public void doCheck(String productNum) {
        String theAction = "";
        theState  = State.process;
        pn  = productNum.trim();
        int amount  = 1;
        try {
            if (theStock.exists(pn)) {
                Product pr = theStock.getDetails(pn);
                if (pr.getQuantity() >= amount) {
                    theAction = String.format("%s : %7.2f (%2d) ",
                            pr.getDescription(),
                            pr.getPrice(),
                            pr.getQuantity());
                    theProduct = pr;
                    theProduct.setQuantity(amount);
                    theState = State.checked;
                } else {
                    theAction = pr.getDescription() + " not in stock";
                }
            } else {
                theAction = "Unknown product number " + pn;
            }
        } catch (StockException e) {
            DEBUG.error("%s\n%s",
                    "CashierModel.doCheck", e.getMessage());
            theAction = e.getMessage();
        }
        saveToUndoStack();  
        setChanged();
        notifyObservers(theAction);
    }

    public void doBuy() {
        String theAction = "";
        int amount  = 1;
        try {
            if (theState != State.checked) {
                theAction = "Check if OK with customer first";
            } else {
                boolean stockBought =
                        theStock.buyStock(
                                theProduct.getProductNum(),
                                theProduct.getQuantity());
                if (stockBought) {
                    makeBasketIfReq();
                    theBasket.add(theProduct);
                    lastAddedProduct = theProduct;  
                    theAction = "Purchased " +
                            theProduct.getDescription();
                } else {
                    theAction = "!!! Not in stock";
                }
            }
        } catch (StockException e) {
            DEBUG.error("%s\n%s",
                    "CashierModel.doBuy", e.getMessage());
            theAction = e.getMessage();
        }
        theState = State.process;
        saveToUndoStack();  
        setChanged();
        notifyObservers(theAction);
    }

    public void doBought() {
        String theAction = "";
        int amount  = 1;
        try {
            if (theBasket != null &&
                    theBasket.size() >= 1) {
                theOrder.newOrder(theBasket);
                theBasket = null;
            }
            theAction = "Next customer";
            theState = State.process;
            theBasket = null;
        } catch (OrderException e) {
            DEBUG.error("%s\n%s",
                    "CashierModel.doCancel", e.getMessage());
            theAction = e.getMessage();
        }
        theBasket = null;
        saveToUndoStack();  
        setChanged();
        notifyObservers(theAction);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            CashierModel previousState = undoStack.pop();
            copyStateFrom(previousState);

            
            if (lastAddedProduct != null && theBasket != null) {
                theBasket.remove(lastAddedProduct);
                lastAddedProduct = null;  
            }

            setChanged();
            notifyObservers("Undo");
        }
    }

    private void copyStateFrom(CashierModel other) {
        this.theState = other.theState;
        this.theProduct = other.theProduct;
        this.theBasket = other.theBasket;
        this.pn = other.pn;
    }

    private void saveToUndoStack() {
        try {
            CashierModel copy = new CashierModel(new RemoteMiddleFactory());
            copy.copyStateFrom(this);
            undoStack.push(copy);
        } catch (Exception e) {
            DEBUG.error("CashierModel.saveToUndoStack\n%s", e.getMessage());
        }
    }

    public void askForUpdate() {
        setChanged();
        notifyObservers("Welcome");
    }

    private void makeBasketIfReq() {
        if (theBasket == null) {
            try {
                int uon = theOrder.uniqueNumber();
                theBasket = makeBasket();
                theBasket.setOrderNum(uon);
            } catch (OrderException e) {
                DEBUG.error("Comms failure\n" +
                        "CashierModel.makeBasket()\n%s", e.getMessage());
            }
        }
    }

    protected Basket makeBasket() {
        return new Basket();
    }
}
