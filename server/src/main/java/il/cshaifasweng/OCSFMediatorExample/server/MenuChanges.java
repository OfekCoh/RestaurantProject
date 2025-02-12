package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;

@Entity
@Table(name = "menu_changes")
public class MenuChanges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int dishId;
    private int newPrice;

    // Constructors
    public MenuChanges() {
    }

    public MenuChanges(int dishId, int newPrice) {
        this.dishId = dishId;
        this.newPrice = newPrice;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }
}