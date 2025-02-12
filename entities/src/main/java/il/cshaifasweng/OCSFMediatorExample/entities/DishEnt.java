package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Arrays;

public class DishEnt implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private String[] ingredients; // Change from List<String> to String[]
    private int price;

    public DishEnt() {}

    public DishEnt(int id, String name, String description, String[] ingredients, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;  // a String[]
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "DishEnt{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ingredients=" + Arrays.toString(ingredients) +
                ", price=" + price +
                '}';
    }
}
