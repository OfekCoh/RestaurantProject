package il.cshaifasweng.OCSFMediatorExample.server;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int price;
    private String name;
    private String description;

    //@ManyToOne
    //@JoinColumn(name = "branch_id", referencedColumnName = "id")
    private int branchID;

    @ElementCollection
    //@CollectionTable(name = "dish_ingredients", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "ingredients")
    private List<String> ingredients;
//    private String ingredients;
    private String image;

//    private static List<Dish> chainDishes = new ArrayList<>();  // chain-wide dishes


    public Dish() {}

    public Dish(int price, String name, String description, int branchID, List<String> ingredients) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.branchID = branchID;
        this.ingredients = ingredients;
        this.image = "";
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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

    public static List<Dish> getChainDishes(org.hibernate.Session session) {
//        chainDishes = session.createQuery("FROM Dish WHERE isChainDish = true", Dish.class).getResultList();  // get chain dishes from db
//        return chainDishes;
        return session.createQuery("FROM Dish WHERE branchID = 0", Dish.class).getResultList();  // get chain dishes from db
    }

    // remove dish by object
    public static void removeDish(Dish dish, org.hibernate.Session session) {
        session.delete(dish);
    }

    // remove dish by id
    public static void removeDish(int id, org.hibernate.Session session) {
        Dish dish = session.get(Dish.class, id);
        if (dish != null) {
            session.delete(dish);
        } else {
            System.out.print("Remove Failed: Dish with id " + id + " not found.\n");
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Dish{" + "id=" + id + "} ";
    }

    

}
