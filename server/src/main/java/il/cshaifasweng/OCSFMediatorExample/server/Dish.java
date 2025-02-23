package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double price;
    private String name;
    private String description;
    private int branchID;

    @ElementCollection
    @Column(name = "ingredients")
    private List<String> ingredients;

    @Lob
    @Column(name = "image", columnDefinition = "LONGTEXT") // Stores Base64 images properly
    private String image;

    private double salePrice;
    private boolean isSalePrice;

    public Dish() {
    }

    public Dish(double price, String name, String description, int branchID, List<String> ingredients, String image) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.branchID = branchID;
        this.ingredients = ingredients;
        this.image = image;
        this.salePrice = 0;
        this.isSalePrice = false;
    }


    public Dish(double price, String name, String description, int branchID, List<String> ingredients, String image, double salePrice, boolean isSalePrice) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.branchID = branchID;
        this.ingredients = ingredients;
        this.image = image;
        this.salePrice = salePrice;
        this.isSalePrice = isSalePrice;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;

    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isSalePrice() {
        return isSalePrice;
    }

    public void setIsSalePrice(boolean isSalePrice) {
        this.isSalePrice = isSalePrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", branchID=" + branchID +
                ", ingredients=" + ingredients +
//                ", image='" + image + '\'' +
                ", salePrice=" + salePrice +
                ", isSalePrice=" + isSalePrice +
                '}';
    }
}
