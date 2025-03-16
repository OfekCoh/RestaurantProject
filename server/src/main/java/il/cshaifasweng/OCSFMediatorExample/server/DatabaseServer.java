package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.Column;
import javax.persistence.criteria.*;
import java.io.InputStream;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


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
            System.out.println("Initializing Database...");
            createDatabase(session);
            insertTestData();// test for roy
            session.getTransaction().commit();
        } catch (Exception exception) {
            System.err.println("Error during initialization: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.password", password);

            // Add ALL of your entities here. You can also try adding a whole package.
            configuration.addAnnotatedClass(RestaurantBranch.class);
            configuration.addAnnotatedClass(Dish.class);
            configuration.addAnnotatedClass(Worker.class);
            configuration.addAnnotatedClass(TableSchema.class);
            configuration.addAnnotatedClass(TableOrder.class);
            configuration.addAnnotatedClass(Order.class);
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
        Dish dish1 = new Dish(15.99, "French Fries", "Crispy golden fries with a side of ketchup.", 0, Arrays.asList("Potatoes", "Salt", "Oil"),new ArrayList<>(), encodeImageToBase64("frenchFries"), 5.99, true);
        Dish dish2 = new Dish(12, "Garlic Bread", "Toasted bread with garlic and butter.", 0, Arrays.asList("Bread", "Butter", "Garlic"), Arrays.asList("Cheese"), encodeImageToBase64("garlicBread"));
        Dish dish3 = new Dish(20, "Spaghetti Bolognese", "Traditional Italian pasta with meat sauce.", 0, Arrays.asList("Pasta", "Ground Beef", "Tomato Sauce", "Parmesan"), Arrays.asList("Olive Oil"), encodeImageToBase64("spaghettiBolognese"));
        Dish dish4 = new Dish(18, "Greek Salad", "Fresh vegetables with feta cheese and olives.", 0, Arrays.asList("Lettuce", "Tomatoes", "Feta Cheese", "Olives"), Arrays.asList("Mozerella Cheese"), encodeImageToBase64("greekSalad"));

        session.save(dish1);
        session.save(dish2);
        session.save(dish3);
        session.save(dish4);
        session.flush();

        // Branch-specific dishes (Branch ID 1)
        Dish dish5 = new Dish(25, "Margherita Pizza", "Classic Italian pizza with fresh tomatoes and basil.", 1, Arrays.asList("Tomato", "Mozzarella", "Basil"),Arrays.asList("Olives", "Mushrooms"), encodeImageToBase64("pizza"));
        Dish dish6 = new Dish(30, "Caesar Salad", "Crispy romaine lettuce with Caesar dressing and parmesan.", 1, Arrays.asList("Lettuce", "Croutons", "Parmesan", "Caesar Dressing"), new ArrayList<>(), encodeImageToBase64("caesarSalad"));
        Dish dish7 = new Dish(28, "Grilled Salmon", "Freshly grilled salmon with lemon butter sauce.", 1, Arrays.asList("Salmon", "Lemon", "Butter", "Garlic"), new ArrayList<>(), encodeImageToBase64("grilledSalmon"));

        session.save(dish5);
        session.save(dish6);
        session.save(dish7);
        session.flush();

        // Branch-specific dishes (Branch ID 2)
        Dish dish8 = new Dish(22, "BBQ Chicken Wings", "Spicy and tangy chicken wings with BBQ sauce.", 2, Arrays.asList("Chicken", "BBQ Sauce", "Spices"), new ArrayList<>(), encodeImageToBase64("bbqChickenWings"));
        Dish dish9 = new Dish(10, "Mozzarella Sticks", "Fried mozzarella sticks with marinara sauce.", 2, Arrays.asList("Mozzarella", "Breadcrumbs", "Marinara Sauce"), new ArrayList<>(), encodeImageToBase64("mozzarellaSticks"));
        Dish dish10 = new Dish(35, "Ribeye Steak", "Juicy ribeye steak served with mashed potatoes.", 2, Arrays.asList("Beef", "Potatoes", "Butter"), new ArrayList<>(), encodeImageToBase64("ribeyeSteak"));

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
        Worker w3 = new Worker("Peter Parker", "peter@company.com", "1234", false, 1, List.of(2)); // role 1 = costumer service
        //Branch Manager
        Worker w4 = new Worker("Charlie Manager", "charlie@company.com", "1234", false, 2, Arrays.asList(1, 2)); // role 2 = branch manager
        //Dietitian
        Worker w5 = new Worker("Alice Dietitian", "alice@company.com", "1234", false, 3, List.of(0));   // role 3 = dietitian
        //Ceo
        Worker w6 = new Worker("Dana CEO", "dana@company.com", "1234", false, 4, List.of(0));   // role 4 = CEO


        session.save(w1);
        session.save(w2);
        session.save(w3);
        session.save(w4);
        session.save(w5);
        session.save(w6);
        session.flush();


        /**
         * Tables Generation
         */

        // Get total number of branches dynamically
        Long numberOfBranches = (Long) session.createQuery("SELECT COUNT(b) FROM RestaurantBranch b").uniqueResult();

        // Create tables for each branch
        for (int branchId = 1; branchId <= numberOfBranches; branchId++) {

            // Fetch the RestaurantBranch entity
            RestaurantBranch branch = session.get(RestaurantBranch.class, branchId);

            if (branch == null) {
                System.out.println("Branch with ID " + branchId + " not found. Skipping...");
                continue;
            }

            List<TableSchema> tables = new ArrayList<>();

            // Creating 10 tables for 2 diners
            for (int i = 0; i < 10; i++) {
                tables.add(new TableSchema(branch, 2, (i % 2 == 0) ? LocationType.INDOOR : LocationType.OUTDOOR));
            }

            // Creating 5 tables for 3 diners
            for (int i = 0; i < 5; i++) {
                tables.add(new TableSchema(branch, 3, (i % 2 == 0) ? LocationType.INDOOR : LocationType.OUTDOOR));
            }

            // Creating 15 tables for 4 diners
            for (int i = 0; i < 15; i++) {
                tables.add(new TableSchema(branch, 4, (i % 2 == 0) ? LocationType.INDOOR : LocationType.OUTDOOR));
            }

            // Save all tables in the database
            for (TableSchema table : tables) {
                session.save(table);
            }

            session.flush();

        }

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

    /**
     * This function encodes images to base64 for the database creation, for starter images
     *
     * @param name - the file name in the starterImages directory
     * @return returns base64 of an image, so it can be loaded into the database.
     */
    public static String encodeImageToBase64(String name) throws Exception {

        try (InputStream is = DatabaseServer.class.getResourceAsStream("./starterImages/" + name + ".jpg")) {
            byte[] fileContent = is.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return base64;
        } catch (Exception e) {
            return null;
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

    public static List<TableSchema> getAllTables() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TableSchema> query = builder.createQuery(TableSchema.class);
            Root<TableSchema> root = query.from(TableSchema.class);

            query.select(root).distinct(true); // Ensure distinct branches
            return session.createQuery(query).getResultList();

        } catch (Exception e) {
            System.err.println("Error fetching tables: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<TableSchema> getTablesWithIds(List<Integer> tableIds) throws Exception {
        if (tableIds == null || tableIds.isEmpty()) {
            System.out.println("DatabaseServer.getTableWithIds- input is null or empty");
            return Collections.emptyList();
        }

        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TableSchema> query = builder.createQuery(TableSchema.class);
            Root<TableSchema> root = query.from(TableSchema.class);

            query.select(root).where(root.get("tableId").in(tableIds)).distinct(true);
            return session.createQuery(query).getResultList();

        } catch (Exception e) {
            System.err.println("Error fetching tables: " + e.getMessage());
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

    // T can be Order or TableOrder
    public static <T> int addOrder(T newOrder) {
        int orderId = -1;
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            orderId = (Integer) session.save(newOrder);

            if (session.contains(newOrder)) { //Check if successfully added
                transaction.commit();
                return orderId;
            } else {
                transaction.rollback();
                System.err.println("Failed to insert order: " + newOrder);
                return orderId;
            }
        } catch (Exception e) {
            System.err.println("Failed to add order: " + e.getMessage());
            e.printStackTrace();
            return orderId;
        }
    }

    public static int addTableOrder(TableOrder newOrder) throws Exception{
        // if its host order there's no risk in losing your seats to other clients
        WhoSubmittedBy whoSubmitted= newOrder.getWhoSubmitted();
        if (whoSubmitted == WhoSubmittedBy.HOSTESS) return addOrder(newOrder);

        // will check if there are still available tables (and other client didn't submit them)
        List<Integer> availableTablesIds= checkAvailableTables(newOrder.getBranchId(), newOrder.getDate(), newOrder.getTime(), newOrder.getNumberOfGuests(), newOrder.getLocation());
        List<TableSchema> tables= getTablesWithIds(availableTablesIds);
        System.out.println("new availableTablesIds: " + availableTablesIds); // just to check in case the tables have been changed

        // if there's no more room return -2, otherwise update the tables in the order and add to database
        if(tables.isEmpty()) return -2;
        else newOrder.setTables(tables);

        return addOrder(newOrder);
    }

    public static int cancelTableOrder(int orderId, String phoneNumber) {
        int newStatus= -1; // -1 fail, 1 free, 2 paid

        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // fetch order
            TableOrder order = session.get(TableOrder.class, orderId);
            if (order == null || order.getStatus() != 0) return -1; // if fetching order failed

            // fetch buyer phone number
            BuyerDetails details = order.getBuyerDetails(); // get phone number
            if (details == null || !details.getPhone().equals(phoneNumber)) return -1; //Check we got the same phone number

            // get order time
            LocalDate orderLocalDate = LocalDate.parse(order.getDate());  // "2025-03-12"
            LocalTime orderLocalTime = LocalTime.parse(order.getTime());  // "hh:mm"
            LocalDateTime orderDateTime = LocalDateTime.of(orderLocalDate, orderLocalTime); // combine them both

            // get time now and calculate if within the last hour
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourBefore = orderDateTime.minusHours(1);

            if(now.isAfter(orderDateTime)) return -1; // cant cancel order that started

            // check if now is within the last hour (oneHourBefore < now < orderStartTim)
            if(now.isAfter(oneHourBefore) && now.isBefore(orderDateTime)) newStatus=2;  // needs to pay a fee
            else newStatus=1; // free cancel

            // Update order status
            order.setStatus(newStatus);
            session.update(order);
            transaction.commit();

            return newStatus;

        } catch (Exception e) {
            System.err.println("Failed to cancel order: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public static Object[] cancelOrder(int orderId, String phoneNumber) {
        int newStatus = -1;
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Order order = session.get(Order.class, orderId);
            if (order == null) {
                return new Object[]{newStatus};
            }

            if (order.getStatus() != 0) {
                //only if the status is pending we can cancel it, if it is other status, its either completed or already canceled
                return new Object[]{newStatus};
            }

            BuyerDetails details = order.getBuyerDetails();
            //Check we got the same phone number
            if (details == null || !details.getPhone().equals(phoneNumber)) {
//                System.err.println("Phone mismatch for Order " + orderId + ". Provided=" + phoneNumber
//                        + ", Actual=" + (details != null ? details.getPhone() : "null"));
                return new Object[]{newStatus};
            }
            // Calculate hours difference from now to the order’s scheduled time
            long nowMillis = System.currentTimeMillis();
            long orderMillis = order.getOrderDate().getTime();
            long diffMillis = orderMillis - nowMillis;

            // the orderDate already passed, immediately return -1
            if (diffMillis <= 0) {
                // The scheduled date/time already passed; cannot cancel for a refund
                transaction.rollback();
                return new Object[]{newStatus};
            }

            // Convert difference in milliseconds to hours (truncating down)
            long diffHours = diffMillis / (1000 * 60 * 60);
            System.out.println("Now Mills: " + nowMillis);
            System.out.println("Order Mills: " + orderMillis);
            System.out.println("diffMillis: " + diffMillis);
            System.out.println("Diff hours: " + diffHours);
            double priceRefund = order.getFinalPrice();
            if (diffHours > 3) {
                newStatus = 1;  // Full Refund
            } else if (diffHours >= 1) {
                newStatus = 2;  // Partial Refund
                priceRefund = priceRefund * 0.5;
            } else {
                newStatus = 3;  // No Refund
                priceRefund = 0;
            }

            // 6) Update the order’s status
            order.setStatus(newStatus);
            session.update(order);
            transaction.commit();
            return new Object[]{newStatus, priceRefund};
        } catch (Exception e) {
            System.err.println("Failed to cancel order: " + e.getMessage());
            e.printStackTrace();
            return new Object[]{newStatus};
        }
    }

    public static List<Integer> checkAvailableTables(int branchId, String date, String time, int numberOfDiners, String location) {
        List<Integer> availableTables = new ArrayList<>();
        List<Integer> numberOfDinersList = new ArrayList<>();  // the indexes will match between the table id and its size

        // for an order at time 10:00 we will check the table is not reserved for 8:45-11:15
        LocalTime requestedTime = LocalTime.parse(time); // "HH:mm" format
        LocalTime startTime = requestedTime.minusMinutes(75);
        LocalTime endTime = requestedTime.plusMinutes(75);

        // Format time for SQL consistency
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTimeStr = startTime.format(formatter);
        String endTimeStr = endTime.format(formatter);

        try (Session session = getSessionFactory().openSession()) {
            // Query to find tables that match the criteria and are NOT in a conflicting order
            String queryStr = """
            SELECT t.tableId FROM TableSchema t
            WHERE t.branch.id = :branchId
            AND t.location = :location
            AND t.tableId NOT IN (
                SELECT t2.tableId FROM TableOrder o
                JOIN o.tables t2
                WHERE o.branchId = :branchId
                AND o.date = :date
                AND o.location = :location
                AND o.time BETWEEN :startTime AND :endTime
            )
            ORDER BY t.numberOfDiners ASC
            """;

            // Create a query and set parameters
            Query<Integer> query = session.createQuery(queryStr, Integer.class);
            query.setParameter("branchId", branchId);
            query.setParameter("location", LocationType.valueOf(location));
            query.setParameter("date", date);
            query.setParameter("startTime", startTimeStr);
            query.setParameter("endTime", endTimeStr);

            // Execute the query and store the result in availableTables
            availableTables = query.getResultList();

            // For every table in the list add its size to numberOfDinersList
            for (Integer tableId : availableTables) {
                TableSchema table = session.get(TableSchema.class, tableId);
                if(table!=null) numberOfDinersList.add(table.getNumberOfDiners());
                else throw new RuntimeException("checkAvailableTables: A table with id " + tableId + " not found");
            }

            return getMinTablesNeeded(availableTables, numberOfDinersList, numberOfDiners); // Return the list of available tables

        } catch (Exception e) {
            System.err.println("Error fetching tables: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<Integer> getMinTablesNeeded(List<Integer> tables_id, List<Integer> tables_num, int diners) {
    // we could make it work with more general table sizes but work explicitly said only 3 given sizes.
        List<Integer> minTablesNeeded = new ArrayList<>(); // the returned list
        // A list of ids of each size table, just for a simpler code.
        List<Integer> ids_with_two = new ArrayList<>();
        List<Integer> ids_with_three = new ArrayList<>();
        List<Integer> ids_with_four = new ArrayList<>();

        // fill the lists
        for(int i=0; i<tables_num.size(); i++) {
            int tableSize = tables_num.get(i); // Get the number of diners for this table
            if (tableSize==2) ids_with_two.add(tables_id.get(i));
            else if (tableSize==3) ids_with_three.add(tables_id.get(i));
            else if(tableSize==4) ids_with_four.add(tables_id.get(i));
        }

        // loop to add tables until all diners are seated
        while(diners>0) {
            if (diners<=2) { // try to add the smallest
                if (!ids_with_two.isEmpty()) {
                    minTablesNeeded.add(ids_with_two.remove(0)); // Pick a table of 2
                    diners -= 2;
                    continue;
                }
                if (!ids_with_three.isEmpty()) {
                    minTablesNeeded.add(ids_with_three.remove(0)); // Pick a table of 3
                    diners -= 3;
                    continue;
                }
                if (!ids_with_four.isEmpty()) {
                    minTablesNeeded.add(ids_with_four.remove(0)); // Pick a table of 4
                    diners -= 4;
                    continue;
                }
            }

            else if (diners==3) { // order to try is 3 4 2
                if (!ids_with_three.isEmpty()) {
                    minTablesNeeded.add(ids_with_three.remove(0)); // Pick a table of 3
                    diners -= 3;
                    continue;
                }
                if (!ids_with_four.isEmpty()) {
                    minTablesNeeded.add(ids_with_four.remove(0)); // Pick a table of 4
                    diners -= 4;
                    continue;
                }
                if (!ids_with_two.isEmpty()) {
                    minTablesNeeded.add(ids_with_two.remove(0)); // Pick a table of 2
                    diners -= 2;
                    continue;
                }
            }

            else if (diners>=4) { // order to try is 4 3 2   (if its exactly 4 and there's no table of 4 we can try to get 2 tables of 2)
                if (!ids_with_four.isEmpty()) {
                    minTablesNeeded.add(ids_with_four.remove(0)); // Pick a table of 4
                    diners -= 4;
                    continue;
                }
                if (!ids_with_three.isEmpty()) {
                    minTablesNeeded.add(ids_with_three.remove(0)); // Pick a table of 3
                    diners -= 3;
                    continue;
                }
                if (!ids_with_two.isEmpty()) {
                    minTablesNeeded.add(ids_with_two.remove(0)); // Pick a table of 2
                    diners -= 2;
                    continue;
                }
            }

            return Collections.emptyList(); // No more tables available so we cant fill the order
        }
        return minTablesNeeded;
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
            existingDish.setToppings(updatedDish.getToppings());
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
        Object[] result = new Object[4];
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

                // Convert workingBranch to a proper ArrayList<Integer> before storing it in result
                List<Integer> branchList = worker.getWorkingBranch();
                result[0] = true;
                result[1] = worker.getId();
                result[2] = worker.getRole();
                result[3] = new ArrayList<>(branchList);  // Make sure that it stored as a List<Integer>

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

    public static void handleComplaint(int complaintId, double refund) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Retrieve the existing complaint
            Complaint existingComplaint = session.get(Complaint.class, complaintId);
            if (existingComplaint == null) {
                System.out.println("Complaint with ID " + complaintId + " does not exist.");

            }

            // Update the status and refund fields
            existingComplaint.setStatus(1);
            existingComplaint.setRefund(refund); // Assuming 'refund' exists in Complaint entity
            // Persist the update
            session.update(existingComplaint);
            transaction.commit();
            System.out.println("Complaint with ID " + complaintId + " handled, refunded amount: " + refund + ", Email sent to: " + existingComplaint.getEmail());// Send mail to client

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public static boolean autoHandleOldComplaints() {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Get current time in UTC using Calendar
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); // Set Calendar to UTC
            cal.add(Calendar.HOUR, -24); // Subtract 24 hours
            Date twentyFourHoursAgo = cal.getTime(); // This gives the Date 24 hours ago in UTC

            // Build a query to retrieve complaints older than 24 hours with status 0 (waiting)
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complaint> selectQuery = builder.createQuery(Complaint.class);
            Root<Complaint> root = selectQuery.from(Complaint.class);
            selectQuery.select(root)
                    .where(
                            builder.equal(root.get("status"), 0), // Only complaints that are waiting
                            builder.lessThan(root.get("date"), twentyFourHoursAgo) // Complaints older than 24 hours
                    );

            // Execute the query and get the list of complaints to be updated
            List<Complaint> oldComplaints = session.createQuery(selectQuery).getResultList();

            if (oldComplaints.isEmpty()) {
                System.out.println("No complaints found that require automatic handling.");
                transaction.commit();
                return false; // No complaints were updated
            }

            // Iterate through each complaint, update its status, and send an email notification
            for (Complaint complaint : oldComplaints) {
                complaint.setStatus(2); // Mark complaint as auto-handled
                session.update(complaint); // Persist the update

                // Send an email notification (assuming getEmail() exists in Complaint)
                System.out.println("Complaint " + complaint.getComplaintId() + " automatically handled, message sent to " + complaint.getEmail());
            }

            // Commit the transaction after processing all complaints
            transaction.commit();
            return true; // Complaints were updated

        } catch (Exception e) {
            System.err.println("Error auto-handling old complaints: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Return false if an error occurs
    }




    public static String getBranchNameById(int branchId) {
        try (Session session = getSessionFactory().openSession()) {
            // Fetch the branch name from the database
            String branchName = session.createQuery(
                            "SELECT r.branchName FROM RestaurantBranch r WHERE r.id = :branchId",
                            String.class)
                    .setParameter("branchId", branchId)
                    .uniqueResult();

            // Return the branch name or a default message if not found
            return (branchName != null) ? branchName : "Branch not found";
        } catch (Exception e) {
            System.err.println("Error retrieving branch name: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------------------------------------------
    // generate report for a branch in a given month, query each relevant DB to get number of things per day in that month
    // to get new report the developer will need to add relevant fiends to BranchReportEnt, add queries to this function and
    // add table to FXML or change queries here and table names in FXML if he just want to change report parameters
    // -----------------------------------------------------------
    public static BranchReportEnt generateBranchReport(int branchId, int year, int month) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            int daysInMonth = getDaysInMonth(year, month);
            List<Integer> ordersPerDay = new ArrayList<>(Collections.nCopies(daysInMonth, 0));
            List<Integer> dinersPerDay = new ArrayList<>(Collections.nCopies(daysInMonth, 0));
            List<Integer> complaintsPerDay = new ArrayList<>(Collections.nCopies(daysInMonth, 0));


            Number failedOrdersResult = session.createQuery(
                            "SELECT COUNT(o) FROM Order o WHERE o.selectedBranch = :branchId " +
                                    "AND YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month " +
                                    "AND o.status NOT IN (0, 4)", Number.class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();
            int failedOrders = (failedOrdersResult != null) ? failedOrdersResult.intValue() : 0;// if null put 0

            // Calculate total income from orders safely
            Number totalOrdersIncomeResult = session.createQuery(
                            "SELECT SUM(o.finalPrice) FROM Order o WHERE o.selectedBranch = :branchId " +
                                    "AND YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month", Number.class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();
            double totalOrdersIncome = (totalOrdersIncomeResult != null) ? totalOrdersIncomeResult.doubleValue() : 0.0;// if null put 0

            // Count complaints handled automatically safely
            Number complaintsHandledAutomaticallyResult = session.createQuery(
                            "SELECT COUNT(c) FROM Complaint c WHERE c.branchId = :branchId " +
                                    "AND YEAR(c.date) = :year AND MONTH(c.date) = :month " +
                                    "AND c.status = 2", Number.class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();
            int complaintsHandledAutomatically = (complaintsHandledAutomaticallyResult != null) ? complaintsHandledAutomaticallyResult.intValue() : 0;// if null put 0

            // Calculate total refunds from complaints as double
            Number totalComplaintsRefundResult = session.createQuery(
                            "SELECT SUM(c.refund) FROM Complaint c WHERE c.branchId = :branchId " +
                                    "AND YEAR(c.date) = :year AND MONTH(c.date) = :month", Number.class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();
            double totalComplaintsRefund = (totalComplaintsRefundResult != null) ? totalComplaintsRefundResult.doubleValue() : 0.0; // if null put 0

            // Query to get orders per day
            List<Object[]> ordersDaily = session.createQuery(
                            "SELECT DAY(o.orderDate), COUNT(o) FROM Order o " +
                                    "WHERE o.selectedBranch = :branchId " +
                                    "AND YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month " +
                                    "GROUP BY DAY(o.orderDate)", Object[].class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .getResultList();

            // Fill the list
            for (Object[] result : ordersDaily) {
                int day = (int) result[0];
                int count = ((Number) result[1]).intValue();
                ordersPerDay.set(day - 1, count);
            }

            // Query to get diners per day
            List<Object[]> dinersDaily = session.createQuery(
                            "SELECT DAY(t.date), SUM(t.numberOfGuests) FROM TableOrder t " +
                                    "WHERE t.branchId = :branchId " +
                                    "AND YEAR(t.date) = :year AND MONTH(t.date) = :month " +
                                    "GROUP BY DAY(t.date)", Object[].class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .getResultList();

            // Fill the list
            for (Object[] result : dinersDaily) {
                int day = (int) result[0];
                int count = ((Number) result[1]).intValue();
                dinersPerDay.set(day - 1, count);
            }

            // Query to get complaints per day
            List<Object[]> complaintsDaily = session.createQuery(
                            "SELECT DAY(c.date), COUNT(c) FROM Complaint c " +
                                    "WHERE c.branchId = :branchId " +
                                    "AND YEAR(c.date) = :year AND MONTH(c.date) = :month " +
                                    "GROUP BY DAY(c.date)", Object[].class)
                    .setParameter("branchId", branchId)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .getResultList();

            // Fill the list
            for (Object[] result : complaintsDaily) {
                int day = (int) result[0];
                int count = ((Number) result[1]).intValue();
                complaintsPerDay.set(day - 1, count);
            }

            transaction.commit();

            // Return the generated report with the updated fields
            return new BranchReportEnt(branchId, year, month, failedOrders, totalComplaintsRefund, totalOrdersIncome, complaintsHandledAutomatically, ordersPerDay, dinersPerDay, complaintsPerDay);
        } catch (Exception e) {
            System.err.println("Error generating branch report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // number of days in a month
    private static int getDaysInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Months are 0-based in Calendar
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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
/// //////////////////////////////////////test for roy
    public static void insertTestData() {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Random random = new Random();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1); // Set to previous month
            int daysInPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            System.out.println("Inserting test data for Branch ID 1...");

            // Insert 10 Orders (Always Branch ID 1)
            for (int i = 0; i < 10; i++) {
                boolean isDelivery = random.nextBoolean();
                calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(daysInPrevMonth) + 1);
                Date orderDate = calendar.getTime();

                BuyerDetails buyer = new BuyerDetails(
                        "Customer" + i, "Address " + i, "123456789" + i,
                        "User" + i, "4111-1111-1111-111" + i, 6, 2025, "123"
                );

                int randomStatus = random.nextInt(5); // Random status between 0 and 4

                Order order = new Order(
                        1, isDelivery, Arrays.asList(101, 102),
                        Arrays.asList("No onions"), buyer, orderDate,
                        20 + (random.nextDouble() * 30)
                );
                order.setStatus(randomStatus);
                session.save(order);
            }

            // Insert 10 Table Orders (Always Branch ID 1)
            for (int i = 0; i < 10; i++) {
                int numberOfGuests = random.nextInt(6) + 1;
                calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(daysInPrevMonth) + 1);
                String date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                String time = "18:00";
                String location = "INDOOR";
                int status = random.nextInt(3); // Random status
                boolean buyerDetailsNeeded = random.nextBoolean();

                BuyerDetails tableBuyer = new BuyerDetails(
                        "Customer" + i, "Table Address " + i, "999888777" + i,
                        "TableUser" + i, "5111-2222-3333-444" + i, 9, 2027, "456"
                );

                TableOrder tableOrder = new TableOrder(
                        1, null, date, time, numberOfGuests, location, status, buyerDetailsNeeded, tableBuyer
                );

                session.save(tableOrder);
            }

            // Insert 10 Complaints (Always Branch ID 1)
            for (int i = 0; i < 10; i++) {
                calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(daysInPrevMonth) + 1);
                Date complaintDate = calendar.getTime();

                BuyerDetails buyer = new BuyerDetails(
                        "Customer" + i, "Address " + i, "987654321" + i,
                        "User" + i, "4000-0000-0000-000" + i, 12, 2026, "999"
                );

                int randomStatus = random.nextInt(3); // Random status between 0 (waiting) and 2 (auto-handled)
                int randomRefund = random.nextInt(100); // Random refund amount

                Complaint complaint = new Complaint(
                        "Issue " + i, complaintDate, 1, buyer, "random email"
                );
                complaint.setStatus(randomStatus);
                complaint.setRefund(randomRefund);

                session.save(complaint);
            }

            transaction.commit();
            System.out.println("Test data inserted successfully for Branch ID 1.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inserting test data: " + e.getMessage());
        }
    }

}



