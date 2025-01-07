package il.cshaifasweng.OCSFMediatorExample.server;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;


public class DatabaseServer {
    private Session session;

    public DatabaseServer(String password){
        try {
            // Initialize Hibernate session
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

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
        configuration.setProperty("hibernate.connection.password", password);

        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(RestaurantBranch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(Dish.class);


        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void createDatabase(Session session) throws Exception {
        generateChainDishes();
        generateBranches();

        // add another special dish to branch1
        RestaurantBranch branch = session.get(RestaurantBranch.class, 1);
        Dish specialDish = new Dish(40,"Cake","goodie goodie chocolate cake.", false, "special love.");
        session.save(specialDish);
        branch.addDishToMenu(specialDish);

//        Dish.removeDish(2,session);
//        List<Dish> allDishes = session.createQuery("FROM Dish", Dish.class).getResultList();
//        for(Dish dish : allDishes) {
//            System.out.print(dish);
//        }
    }

    private static void generateChainDishes() throws Exception {
        String[] dishNames = {"Spicy Garlic Noodles",
                "Grilled Lemon Herb Chicken",
                "Truffle Mushroom Risotto",
                "Classic Margherita Pizza",
                "Crispy Honey Glazed Salmon"};
        int[] prices = {32,49,45,38,40};
        String[] descriptions = {"Savory noodles tossed in a spicy garlic sauce.",
                "Juicy chicken grilled with lemon and herbs.",
                "Creamy risotto with truffle and mushrooms.",
                "Traditional pizza with fresh mozzarella and basil.",
                "Salmon fillet glazed with sweet honey and seared to perfection."};

        // Create branches with default chain menu
        for (int i = 0; i < dishNames.length; i++) {
            Dish chainDish = new Dish(prices[i],dishNames[i],descriptions[i],true,"normal dish, not spicy.");

//            Dish.addChainDish(chainDish);  // Add the dish to the static chain-wide dishes list

            session.save(chainDish);
            session.flush();
        }
    }

    private static void generateBranches() throws Exception {
        String[] branchNames = {"First & Best Branch"};
        String[] locations = {"boulevard of broken dreams"};

        // Create branches with default chain menu
        for (int i = 0; i < branchNames.length; i++) {
            Menu chainMenu = new Menu();
            RestaurantBranch branch = new RestaurantBranch(branchNames[i],locations[i],chainMenu);

            session.save(chainMenu);
            session.save(branch);
            /*
             * The call to session.flush() updates the DB immediately without ending the transaction.
             * Recommended to do after an arbitrary unit of work.
             * MANDATORY to do if you are saving a large amount of data - otherwise you may get cache errors.
             */
            session.flush();
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
