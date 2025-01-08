package il.cshaifasweng.OCSFMediatorExample.server;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "menu", cascade = CascadeType.ALL)
    private RestaurantBranch branch;

    @ManyToMany
    @JoinTable(
        name = "menu_dishes",
        joinColumns = @JoinColumn(name = "menu_id"),
        inverseJoinColumns = @JoinColumn(name = "dish_id") )
    private List<Dish> dishes = new ArrayList<>();  // local branch dishes


    public Menu() {}

    public int getId() {
        return id;
    }

    public RestaurantBranch getBranch() {
        return branch;
    }

    // only used from branch method- setMenu()
    public void setBranch(RestaurantBranch branch) {
        this.branch = branch;
    }

    // only usd from branch method- getAllDishesInBranch()
    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public void addDishToMenu(Dish dish) {
        if (!dishes.contains(dish)) {
            dishes.add(dish);
        }
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
    }

    @Override
    public String toString() {
        return "Menu{" + "id=" + id + '}';
    }

}
