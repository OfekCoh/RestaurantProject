package il.cshaifasweng.OCSFMediatorExample.server;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.server.DishConverter.convertToDishEntList;


public class DatabaseServer {
    private static Session session;
    public static String password;

    public DatabaseServer(String password){
        try {
            DatabaseServer.password =password;
            // Initialize Hibernate session
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();
            if (!session.createQuery("FROM Dish", Dish.class).getResultList().isEmpty()) {
                System.out.println("Database already populated. Skipping initialization.");
                return;
            }
            // start code here
            createDatabase(session);
            printAllBranches(session);


            // end code here
            session.getTransaction().commit(); // Save everything.

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        }

        session.close();  // idk if it should be here

    }

    private static SessionFactory getSessionFactory(String password) throws HibernateException {
        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.password", password);

        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(RestaurantBranch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(Dish.class);


        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void createDatabase(Session session) throws Exception {
        /**
         * Branches Generation
         */

        Menu menu1 = new Menu();
        Menu menu2 = new Menu();

        RestaurantBranch branch1 = new RestaurantBranch("Downtown Branch", "123 Main St, Cityville", menu1);
        RestaurantBranch branch2 = new RestaurantBranch("Uptown Branch", "456 Elm St, Metro City", menu2);


        session.save(branch1);
        session.save(branch2);
        session.flush();

        /**
         * Dish Generation
         */

        // Chain-wide dishes (Branch ID 0)
        Dish dish1 = new Dish(15, "French Fries", "Crispy golden fries with a side of ketchup.", 0, Arrays.asList("Potatoes", "Salt", "Oil"));
        Dish dish2 = new Dish(12, "Garlic Bread", "Toasted bread with garlic and butter.", 0, Arrays.asList("Bread", "Butter", "Garlic"));
        Dish dish3 = new Dish(20, "Spaghetti Bolognese", "Traditional Italian pasta with meat sauce.", 0, Arrays.asList("Pasta", "Ground Beef", "Tomato Sauce", "Parmesan"));
        Dish dish4 = new Dish(18, "Greek Salad", "Fresh vegetables with feta cheese and olives.", 0, Arrays.asList("Lettuce", "Tomatoes", "Feta Cheese", "Olives"));

        session.save(dish1);
        session.save(dish2);
        session.save(dish3);
        session.save(dish4);
        session.flush();

        // Branch-specific dishes (Branch ID 1)
        Dish dish5 = new Dish(25, "Margherita Pizza", "Classic Italian pizza with fresh tomatoes and basil.", 1, Arrays.asList("Tomato", "Mozzarella", "Basil"));
        Dish dish6 = new Dish(30, "Caesar Salad", "Crispy romaine lettuce with Caesar dressing and parmesan.", 1, Arrays.asList("Lettuce", "Croutons", "Parmesan", "Caesar Dressing"));
        Dish dish7 = new Dish(28, "Grilled Salmon", "Freshly grilled salmon with lemon butter sauce.", 1, Arrays.asList("Salmon", "Lemon", "Butter", "Garlic"));

        session.save(dish5);
        session.save(dish6);
        session.save(dish7);
        session.flush();

        // Branch-specific dishes (Branch ID 2)
        Dish dish8 = new Dish(22, "BBQ Chicken Wings", "Spicy and tangy chicken wings with BBQ sauce.", 2, Arrays.asList("Chicken", "BBQ Sauce", "Spices"));
        Dish dish9 = new Dish(10, "Mozzarella Sticks", "Fried mozzarella sticks with marinara sauce.", 2, Arrays.asList("Mozzarella", "Breadcrumbs", "Marinara Sauce"));
        Dish dish10 = new Dish(35, "Ribeye Steak", "Juicy ribeye steak served with mashed potatoes.", 2, Arrays.asList("Beef", "Potatoes", "Butter"));

        session.save(dish8);
        session.save(dish9);
        session.save(dish10);
        session.flush();

//        generateChainDishes();
//        generateBranches();

        // add another special dish to branch1
//        RestaurantBranch branch = session.get(RestaurantBranch.class, 1);
//        Dish specialDish = new Dish(40,"Cake","goodie goodie chocolate cake.", 1, Arrays.asList("special love."));
//        session.save(specialDish);
//        branch.addDishToMenu(specialDish);

//        Dish.removeDish(2,session);
//        List<Dish> allDishes = session.createQuery("FROM Dish", Dish.class).getResultList();
//        for(Dish dish : allDishes) {
//            System.out.print(dish);
//        }
    }


    public static List<Dish> getAllDishes(Session session) {
        // now we accept a session parameter
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Dish> query = builder.createQuery(Dish.class);
        query.from(Dish.class);
        return session.createQuery(query).getResultList();
    }
    public static List<DishEnt> getMenu() throws Exception {
        List<DishEnt> dishes = null;

        try (Session session = getSessionFactory(DatabaseServer.password).openSession()) {
            session.beginTransaction();

            // pass the *local* session, not the static field
            List<Dish> allDishes = getAllDishes(session);
            dishes = convertToDishEntList(allDishes);

            session.getTransaction().commit();
        }

        return dishes;
    }


    /*
     * This method updates the price of the specific dish with the id to the new price
     * @param id - the id of the dish
     * @param price - the new price for the dish
     * @throws Exception - in case we didn't succeed in updating
     */
    public static void updatePriceForDish(int id, int price) throws Exception {
        Session session = null; // Ensure session is managed correctly
        try {


            SessionFactory sessionFactory = getSessionFactory(DatabaseServer.password);
            session = sessionFactory.openSession();
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Dish> criteriaUpdate = builder.createCriteriaUpdate(Dish.class);
            // Define the root for the entity
            Root<Dish> root = criteriaUpdate.from(Dish.class);
            // Set the update clause
            criteriaUpdate.set(root.get("price"), price);
            // Set the where clause
            criteriaUpdate.where(builder.equal(root.get("id"), id));
            // Execute the update query
            int affectedRows = session.createQuery(criteriaUpdate).executeUpdate();
            // Commit the transaction
            session.getTransaction().commit();
            session.close();
        }
        catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        }
    }

    private static <T> List<T> getAllEntities(Class<T> entityClass) throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        query.from(entityClass);
        List<T> data = session.createQuery(query).getResultList();
        return data;
    }

    private static void printAllBranches(Session session) throws Exception {
        List<RestaurantBranch> branches = getAllEntities(RestaurantBranch.class);
        for (RestaurantBranch branch : branches) {
            System.out.print( branch.getId()+". Branch '"+branch.getBranchName()+"', in '"+branch.getLocation()+"'.\n");
            System.out.print("     Menu:\n");
            List<Dish> dishes= branch.getAllDishesInBranch(session);
            for (Dish dish : dishes) {
                System.out.print( "     [" + dish.getId()+"]:   " + dish.getPrice() + "$   "  + dish.getName()+"  --  " + dish.getDescription() + "\n");
            }
        }
    }
}
