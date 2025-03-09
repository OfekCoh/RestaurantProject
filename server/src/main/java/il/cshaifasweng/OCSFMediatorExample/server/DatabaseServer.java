package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.*;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.server.Convertor.*;


public class DatabaseServer {
    public static String password;
    private static SessionFactory sessionFactory;


    public DatabaseServer(String password) {
        DatabaseServer.password = password;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            if (!session.createQuery("FROM Dish", Dish.class).getResultList().isEmpty()) {
                System.out.println("Database already populated. Skipping initialization.");
                userLogoutAll();
                return;
            }
            createDatabase(session);
            session.getTransaction().commit();
        } catch (Exception exception) {
            System.err.println("Error during initialization: " + exception.getMessage());
            exception.printStackTrace();
        }
    }


    private static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(RestaurantBranch.class);
            configuration.addAnnotatedClass(Dish.class);
            configuration.addAnnotatedClass(Worker.class);
            configuration.addAnnotatedClass(TableSchema.class);
            configuration.addAnnotatedClass(TableOrder.class);
            configuration.addAnnotatedClass(Complaint.class);
            configuration.addAnnotatedClass(MenuChanges.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }


    private static void createDatabase(Session session) throws Exception {
        /**
         * Branches Generation
         */

        RestaurantBranch branch1 = new RestaurantBranch("Downtown Branch", "123 Main St, Cityville", Arrays.asList("08:00-16:00", "08:00-16:00", "08:00-16:00", "08:00-18:00", "08:00-22:00", "09:00-22:00", "Closed"));
        RestaurantBranch branch2 = new RestaurantBranch("Uptown Branch", "456 Elm St, Metro City", Arrays.asList("08:00-16:00", "08:00-16:00", "08:00-16:00", "08:00-18:00", "08:00-22:00", "Closed", "Closed"));

        session.save(branch1);
        session.save(branch2);
        session.flush();

        /**
         * Dish Generation
         */

        // Chain-wide dishes (Branch ID 0)
        Dish dish1 = new Dish(15.99, "French Fries", "Crispy golden fries with a side of ketchup.", 0, Arrays.asList("Potatoes", "Salt", "Oil"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=", 5.99, true);
        Dish dish2 = new Dish(12, "Garlic Bread", "Toasted bread with garlic and butter.", 0, Arrays.asList("Bread", "Butter", "Garlic"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish3 = new Dish(20, "Spaghetti Bolognese", "Traditional Italian pasta with meat sauce.", 0, Arrays.asList("Pasta", "Ground Beef", "Tomato Sauce", "Parmesan"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish4 = new Dish(18, "Greek Salad", "Fresh vegetables with feta cheese and olives.", 0, Arrays.asList("Lettuce", "Tomatoes", "Feta Cheese", "Olives"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");

        session.save(dish1);
        session.save(dish2);
        session.save(dish3);
        session.save(dish4);
        session.flush();

        // Branch-specific dishes (Branch ID 1)
        Dish dish5 = new Dish(25, "Margherita Pizza", "Classic Italian pizza with fresh tomatoes and basil.", 1, Arrays.asList("Tomato", "Mozzarella", "Basil"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish6 = new Dish(30, "Caesar Salad", "Crispy romaine lettuce with Caesar dressing and parmesan.", 1, Arrays.asList("Lettuce", "Croutons", "Parmesan", "Caesar Dressing"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish7 = new Dish(28, "Grilled Salmon", "Freshly grilled salmon with lemon butter sauce.", 1, Arrays.asList("Salmon", "Lemon", "Butter", "Garlic"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");

        session.save(dish5);
        session.save(dish6);
        session.save(dish7);
        session.flush();

        // Branch-specific dishes (Branch ID 2)
        Dish dish8 = new Dish(22, "BBQ Chicken Wings", "Spicy and tangy chicken wings with BBQ sauce.", 2, Arrays.asList("Chicken", "BBQ Sauce", "Spices"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish9 = new Dish(10, "Mozzarella Sticks", "Fried mozzarella sticks with marinara sauce.", 2, Arrays.asList("Mozzarella", "Breadcrumbs", "Marinara Sauce"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");
        Dish dish10 = new Dish(35, "Ribeye Steak", "Juicy ribeye steak served with mashed potatoes.", 2, Arrays.asList("Beef", "Potatoes", "Butter"), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAm8B8E9zX9EAAAAASUVORK5CYII=");

        session.save(dish8);
        session.save(dish9);
        session.save(dish10);
        session.flush();


        /**
         * Workers Generation
         */

        //Regular Worker
        Worker w1 = new Worker("Bob Regular", "bob@company.com", "1234", false, 0, List.of(1));   // role 0 = regular
        Worker w2 = new Worker("Eve Regular", "eve@company.com", "1234", false, 0, List.of(2));    // already logged in
        //Costumer support
        Worker w3 = new Worker("Peter Parker", "peter@company.com", "1234", false, 1, List.of(3));
        //Branch Manager
        Worker w4 = new Worker("Charlie Manager", "charlie@company.com", "1234", false, 2, Arrays.asList(1, 2)); // role 1 = branch manager
        //Dietitian
        Worker w5 = new Worker("Alice Dietitian", "alice@company.com", "1234", false, 3, List.of(0));   // role 2 = dietitian
        //Ceo
        Worker w6 = new Worker("Dana CEO", "dana@company.com", "1234", false, 4, List.of(0));   // role 3 = CEO


        session.save(w1);
        session.save(w2);
        session.save(w3);
        session.save(w4);
        session.save(w5);
        session.save(w6);
        session.flush();

    }

    public static boolean addComplaint(Complaint complaint) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(complaint);

            if (session.contains(complaint)) { //Check if successfully added
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                System.err.println("Failed to insert complaint: " + complaint);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to add complaint: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    public static List<RestaurantBranch> getAllBranches(Session session) throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RestaurantBranch> query = builder.createQuery(RestaurantBranch.class);
        Root<RestaurantBranch> root = query.from(RestaurantBranch.class);

        query.select(root).distinct(true); // Ensure distinct branches

        List<RestaurantBranch> branches = session.createQuery(query).getResultList();

        // Force Hibernate to fully load openingHours before session closes
        for (RestaurantBranch branch : branches) {
            Hibernate.initialize(branch.getOpeningHours()); // Load all days correctly
        }

        return branches;
    }

    public static List<MenuChanges> getAllMenuChanges(Session session) throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MenuChanges> query = builder.createQuery(MenuChanges.class);
        Root<MenuChanges> root = query.from(MenuChanges.class);

        query.select(root).distinct(true); // Ensure distinct branches

        List<MenuChanges> menuChanges = session.createQuery(query).getResultList();

        return menuChanges;
    }

    public static List<Complaint> getAllComplaints(Session session) throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Complaint> query = builder.createQuery(Complaint.class);
        Root<Complaint> root = query.from(Complaint.class);

        query.select(root).distinct(true); // Ensure distinct branches

        List<Complaint> complaintsList = session.createQuery(query).getResultList();

        return complaintsList;
    }

    public static List<ComplaintEnt> getComplaints() {
        try (Session session = getSessionFactory().openSession()) {
            List<Complaint> complaintsList = getAllComplaints(session);
            return Convertor.convertToComplaintEntList(complaintsList);
        } catch (Exception e) {
            System.err.println("Error fetching complaints: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public static List<BranchEnt> getBranches() {
        try (Session session = getSessionFactory().openSession()) {
            List<RestaurantBranch> allBranches = getAllBranches(session);
            return convertToBranchEntList(allBranches);
        } catch (Exception e) {
            System.err.println("Error fetching branches: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<MenuChangeEnt> getMenuChanges() {

        try (Session session = getSessionFactory().openSession()) {
            List<MenuChanges> allMenuChanges = getAllMenuChanges(session);
            return convertToMenuChangesEntList(allMenuChanges);
        } catch (Exception e) {
            System.err.println("Error fetching branches: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public static List<Dish> getAllDishes(Session session) {
        // now we accept a session parameter
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Dish> query = builder.createQuery(Dish.class);
        query.from(Dish.class);
        return session.createQuery(query).getResultList();
    }

    public static List<DishEnt> getMenu() {
        try (Session session = getSessionFactory().openSession()) {
            List<Dish> allDishes = getAllDishes(session);
            return convertToDishEntList(allDishes);
        } catch (Exception e) {
            System.err.println("Error fetching menu: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public static boolean addMenuChange(MenuChanges newMenuChanges) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(newMenuChanges);

            if (session.contains(newMenuChanges)) { //Check if successfully added
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                System.err.println("Failed to insert menuChange: " + newMenuChanges);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to add menuChange: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addDish(Dish newDish) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(newDish);

            if (session.contains(newDish)) { //Check if successfully added
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                System.err.println("Failed to insert dish: " + newDish);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to add dish: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDish(Dish updatedDish) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 1) Retrieve the existing dish from DB
            Dish existingDish = session.get(Dish.class, updatedDish.getId());
            if (existingDish == null) {
                System.out.println("Dish with ID " + updatedDish.getId() + " does not exist.");
                return false;
            }

            // 2) Update fields from the 'updatedDish' object
            existingDish.setName(updatedDish.getName());
            existingDish.setDescription(updatedDish.getDescription());
            existingDish.setBranchID(updatedDish.getBranchID());
            existingDish.setIngredients(updatedDish.getIngredients());
            existingDish.setImage(updatedDish.getImage());
            existingDish.setPrice(updatedDish.getPrice());
            existingDish.setSalePrice(updatedDish.getSalePrice());
            existingDish.setIsSalePrice(updatedDish.isSalePrice());

            // 3) Persist in DB
            session.update(existingDish);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }


    /*
     * This method updates the price of the specific dish with the id to the new price
     * @param id - the id of the dish
     * @param price - the new price for the dish
     * @throws Exception - in case we didn't succeed in updating
     */
    public static void updatePriceForDish(int id, double price, boolean isSalePrice, double salePrice) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Dish> criteriaUpdate = builder.createCriteriaUpdate(Dish.class);
            Root<Dish> root = criteriaUpdate.from(Dish.class);
            criteriaUpdate.set(root.get("price"), price);
            criteriaUpdate.set(root.get("isSalePrice"), isSalePrice);
            criteriaUpdate.set(root.get("salePrice"), salePrice);
            criteriaUpdate.where(builder.equal(root.get("id"), id));

            session.createQuery(criteriaUpdate).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static void deleteMenuChange(int id) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            MenuChanges menuChange = session.get(MenuChanges.class, id);

            if (menuChange != null) {
                session.remove(menuChange);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * This method updates the branchID of the specific dish with the id to the new branch
     * @param id - the id of the dish
     * @param branchId - the new branchID for the dish
     * @throws Exception - in case we didn't succeed in updating
     */
    public static void updateBranchForDish(int id, int branchID) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Dish> criteriaUpdate = builder.createCriteriaUpdate(Dish.class);
            Root<Dish> root = criteriaUpdate.from(Dish.class);

            criteriaUpdate.set(root.get("branchID"), branchID);
            criteriaUpdate.where(builder.equal(root.get("id"), id));

            session.createQuery(criteriaUpdate).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            System.err.println("Error updating dish branch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteDish(int id) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Dish dish = session.get(Dish.class, id);

            if (dish != null) {
                session.remove(dish);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Object[] userLogin(String email, String password) {
        Object[] result = new Object[3];
        result[0] = false;
        result[1] = -1;

        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Worker> criteriaQuery = builder.createQuery(Worker.class);
            Root<Worker> root = criteriaQuery.from(Worker.class);
            criteriaQuery.select(root).where(
                    builder.equal(root.get("email"), email),
                    builder.equal(root.get("password"), password)
            );

            Worker worker = session.createQuery(criteriaQuery).uniqueResult();

            if (worker != null && !worker.isLoggedIn()) {
                CriteriaUpdate<Worker> criteriaUpdate = builder.createCriteriaUpdate(Worker.class);
                Root<Worker> updateRoot = criteriaUpdate.from(Worker.class);
                criteriaUpdate.set(updateRoot.get("isLoggedIn"), true);
                criteriaUpdate.where(builder.equal(updateRoot.get("id"), worker.getId()));

                session.createQuery(criteriaUpdate).executeUpdate();
                result[0] = true;
                result[1] = worker.getId();
                result[2] = worker.getRole();
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static Object[] userLogout(int workerId) {
        Object[] result = new Object[1];
        result[0] = false;

        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Worker> criteriaQuery = builder.createQuery(Worker.class);
            Root<Worker> root = criteriaQuery.from(Worker.class);

            criteriaQuery.select(root).where(builder.equal(root.get("id"), workerId));
            Worker worker = session.createQuery(criteriaQuery).uniqueResult();

            if (worker != null && worker.isLoggedIn()) {
                CriteriaUpdate<Worker> criteriaUpdate = builder.createCriteriaUpdate(Worker.class);
                Root<Worker> updateRoot = criteriaUpdate.from(Worker.class);

                criteriaUpdate.set(updateRoot.get("isLoggedIn"), false);
                criteriaUpdate.where(builder.equal(updateRoot.get("id"), worker.getId()));

                session.createQuery(criteriaUpdate).executeUpdate();
                result[0] = true;
            }

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Error logging out user: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    public static void userLogoutAll() {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Worker> criteriaUpdate = builder.createCriteriaUpdate(Worker.class);
            Root<Worker> root = criteriaUpdate.from(Worker.class);

            criteriaUpdate.set(root.get("isLoggedIn"), false);
            session.createQuery(criteriaUpdate).executeUpdate();

            transaction.commit();
            System.out.println("All workers logged out successfully!");
        } catch (Exception e) {
            System.err.println("Error logging out all users: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    private static <T> List<T> getAllEntities(Class<T> entityClass) throws Exception {
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<T> query = builder.createQuery(entityClass);
//        query.from(entityClass);
//        List<T> data = session.createQuery(query).getResultList();
//        return data;
//    }

//    private static void printAllBranches(Session session) throws Exception {
//        List<RestaurantBranch> branches = getAllEntities(RestaurantBranch.class);
//        for (RestaurantBranch branch : branches) {
//            System.out.print(branch.getId() + ". Branch '" + branch.getBranchName() + "', in '" + branch.getLocation() + "'.\n");
//            System.out.print("     Menu:\n");
//            List<Dish> dishes = branch.getAllDishesInBranch(session);
//            for (Dish dish : dishes) {
//                System.out.print("     [" + dish.getId() + "]:   " + dish.getPrice() + "$   " + dish.getName() + "  --  " + dish.getDescription() + "\n");
//            }
//        }
//    }
}
