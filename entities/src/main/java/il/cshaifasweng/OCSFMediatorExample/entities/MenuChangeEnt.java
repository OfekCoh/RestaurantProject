package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class MenuChangeEnt implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int dishId;
    private double oldPrice;
    private double newPrice;
    private boolean oldIsOnSale;
    private boolean newIsOnSale;
    private double oldSalePrice;
    private double newSalePrice;

    // Constructors
    public MenuChangeEnt() {
    }

    public MenuChangeEnt(int dishId, double oldPrice, boolean oldIsOnSale, double oldSalePrice, double newPrice, boolean newIsOnSale, double newSalePrice) {
        this.dishId = dishId;
        this.oldPrice = oldPrice;
        this.oldIsOnSale = oldIsOnSale;
        this.oldSalePrice = oldSalePrice;
        this.newPrice = newPrice;
        this.newIsOnSale = newIsOnSale;
        this.newSalePrice = newSalePrice;
    }

    public MenuChangeEnt(int id, int dishId, double oldPrice, boolean oldIsOnSale, double oldSalePrice, double newPrice, boolean newIsOnSale, double newSalePrice) {
        this.id = id;
        this.dishId = dishId;
        this.oldPrice = oldPrice;
        this.oldIsOnSale = oldIsOnSale;
        this.oldSalePrice = oldSalePrice;
        this.newPrice = newPrice;
        this.newIsOnSale = newIsOnSale;
        this.newSalePrice = newSalePrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public boolean isOldIsOnSale() {
        return oldIsOnSale;
    }

    public void setOldIsOnSale(boolean oldIsOnSale) {
        this.oldIsOnSale = oldIsOnSale;
    }

    public boolean isNewIsOnSale() {
        return newIsOnSale;
    }

    public void setNewIsOnSale(boolean newIsOnSale) {
        this.newIsOnSale = newIsOnSale;
    }

    public double getOldSalePrice() {
        return oldSalePrice;
    }

    public void setOldSalePrice(double oldSalePrice) {
        this.oldSalePrice = oldSalePrice;
    }

    public double getNewSalePrice() {
        return newSalePrice;
    }

    public void setNewSalePrice(double newSalePrice) {
        this.newSalePrice = newSalePrice;
    }

    @Override
    public String toString() {
        return "MenuChanges{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", oldPrice=" + oldPrice +
                ", newPrice=" + newPrice +
                ", oldIsOnSale=" + oldIsOnSale +
                ", newIsOnSale=" + newIsOnSale +
                ", oldSalePrice=" + oldSalePrice +
                ", newSalePrice=" + newSalePrice +
                '}';
    }
}
