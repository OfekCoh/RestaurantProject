package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Arrays;

public class DishEnt implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private String[] ingredients; // Change from List<String> to String[]
    private String[] toppings;
    private String image;
    private double price;
    private int branchID;

    private double salePrice;
    private boolean isSalePrice;

    public DishEnt() {
    }

    //To send to the server side, since the database assign the id.
    public DishEnt(String name, String description, String[] ingredients, String[]toppings, double price, int branchID, String image, boolean isSalePrice, double salePrice) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;  // a String[]!
        this.toppings=toppings;
        this.price = price;
        this.branchID = branchID;
        this.image = image;
        this.isSalePrice = isSalePrice;
        this.salePrice = salePrice;
    }

    public DishEnt(int id, String name, String description, String[] ingredients,String[] toppings, double price, int branchID, String image, boolean isSalePrice, double salePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;  // a String[]!
        this.toppings=toppings;
        this.price = price;
        this.branchID = branchID;
        this.image = image;
        this.isSalePrice = isSalePrice;
        this.salePrice = salePrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getIngredients() {  // Updated to return an array
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getToppings() {
        return toppings;
    }

    public void setToppings(String[] toppings) {
        this.toppings = toppings;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBranchID() {
        return branchID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public boolean getIsSalePrice() {
        return isSalePrice;
    }

    public void setSalePrice(boolean salePriceActive) {
        this.isSalePrice = salePriceActive;
    }

    @Override
    public String toString() {
        return "DishEnt{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ingredients=" + Arrays.toString(ingredients) +
                ", price=" + price +
                ", branchID=" + branchID +
                ", toppings=" + Arrays.toString(toppings) +
//                ", image='" + image + '\'' +
                ", salePrice=" + salePrice +
                ", isSalePrice=" + isSalePrice +
                '}';
    }


}
