package il.cshaifasweng.OCSFMediatorExample.server;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;


@Entity
@Table(name = "branches")
public class RestaurantBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String branchName;
    private String location;

    // cascade- changes update to both, orphan- if no branch menu is deleted
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "menu_id", referencedColumnName = "id")
    private Menu menu;

    public RestaurantBranch() {}

    public RestaurantBranch(String branchName, String location, Menu menu) {
        this.branchName = branchName;
        this.location = location;
        this.menu = menu;
        if (menu != null) {
            menu.setBranch(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Dish> getAllDishesInBranch(org.hibernate.Session session) {
        List<Dish> allDishes = new ArrayList<>(Dish.getChainDishes(session)); // Add chain-wide dishes
        allDishes.addAll(menu.getDishes());                           // Add branch-specific dishes
        return allDishes;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            menu.setBranch(this);
        }
    }

    public void addDishToMenu(Dish dish) {
        menu.addDishToMenu(dish);
    }

    public void removeDish(Dish dish) {
        menu.removeDish(dish);
    }

}
