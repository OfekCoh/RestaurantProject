package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class MenuEnt implements Serializable {

    private static final long serialVersionUID = -8224097662914849956L;

    private List<DishEnt> dishes;

    public MenuEnt() {}

    public MenuEnt(List<DishEnt> dishes) {
        this.dishes = dishes;
    }

    public List<DishEnt> getListDishes() {
        return dishes;
    }

    public void setDishes(List<DishEnt> dishes) {
        this.dishes = dishes;
    }

    public DishEnt getDish(int id) {
        for (DishEnt dish : dishes) {
            if (dish.getId() == id) {
                return dish;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "dishes=" + dishes +
                '}';
    }
}