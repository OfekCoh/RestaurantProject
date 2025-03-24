package il.cshaifasweng.OCSFMediatorExample.server;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.dom4j.Branch;

import javax.xml.crypto.Data;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private final int schedulerIntervals = 1;// minuets between each checks interval
    private DatabaseServer databaseServer;
    private int maxBranchId;
    public SimpleServer(int port, String databasePassword) {
        super(port);
        DatabaseServer.password = databasePassword;
        // create the database
        databaseServer = new DatabaseServer(databasePassword);
        maxBranchId = DatabaseServer.getMaxBranchId();
        startChecksScheduler(); // Start automatic checks
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        // 1) Check if we received an OCSFMessage
        if (msg instanceof Message) {
            Message message = (Message) msg;
            String command = message.getCommand();
            Object[] payload = message.getPayload();

            switch (command) {
                // -----------------------------------------------------------
                // #warning
                // -----------------------------------------------------------
                case "#warning":
                    Warning warning = new Warning("Warning from server!");
                    try {
                        client.sendToClient(warning);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
                    break;

                // -----------------------------------------------------------
                // add client
                // empty payload
                // -----------------------------------------------------------
                case "add client": {
                    try {
                        SubscribedClient connection = new SubscribedClient(client);
                        SubscribersList.add(connection);
                        client.sendToClient("client added successfully");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                // -----------------------------------------------------------
                // remove client
                // empty payload
                // -----------------------------------------------------------
                case "remove client": {
                    if (!SubscribersList.isEmpty()) {
                        for (SubscribedClient subscribedClient : SubscribersList) {
                            if (subscribedClient.getClient().equals(client)) {
                                SubscribersList.remove(subscribedClient);
                                break;
                            }
                        }
                    }
                    break;
                }

                // -----------------------------------------------------------
                // getMenu
                // -----------------------------------------------------------
                case "getMenu": {
                    try {
                        List<BranchEnt> branches = DatabaseServer.getBranches();
                        List<DishEnt> dishes = DatabaseServer.getMenu();

                        Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                        client.sendToClient(menuResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "get menuChanges": {
                    try {
                        List<MenuChangeEnt> menuChanges = DatabaseServer.getMenuChanges();

                        Message menuResponse = new Message("menuChange", new Object[]{menuChanges});
                        client.sendToClient(menuResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "add menuChanges": {
                    if (payload.length == 7) {
                        int dishId = (int) payload[0];
                        double oldPrice = (double) payload[1];
                        boolean oldIsOnSale = (boolean) payload[2];
                        double oldSalePrice = (double) payload[3];
                        double newPrice = (double) payload[4];
                        boolean newIsOnSale = (boolean) payload[5];
                        double newSalePrice = (double) payload[6];

                        MenuChanges newMenu = new MenuChanges(dishId, oldPrice, oldIsOnSale, oldSalePrice, newPrice, newIsOnSale, newSalePrice);

                        try {
                            Boolean result = DatabaseServer.addMenuChange(newMenu);

                            if (result) {
                                Warning successMsg = new Warning("Successfully added menuChange!");
                                client.sendToClient(successMsg);
                                //Sending everyone the updatedMenuChanges.
                                List<MenuChangeEnt> menuChanges = DatabaseServer.getMenuChanges();
                                Message menuResponse = new Message("menuChange", new Object[]{menuChanges});
                                client.sendToClient(menuResponse);
                            } else {
                                Warning failMsg = new Warning("Failed to add menuChange!");
                                client.sendToClient(failMsg);
                            }

                        } catch (IOException e) {
                            System.err.println("IOException: " + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.err.println("Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Warning failMsg = new Warning("Failed to add dish!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "set menuChanges": {
                    if (payload.length == 6) {
                        int menuChangeID = (int) payload[0];
                        int dishID = (int) payload[1];
                        double newPrice = (double) payload[2];
                        boolean newIsOnSale = (boolean) payload[3];
                        double newSalePrice = (double) payload[4];
                        boolean status = (boolean) payload[5]; //Approve or not-approved

                        if (status) { //Change the price - approved
                            DatabaseServer.updatePriceForDish(dishID, newPrice, newIsOnSale, newSalePrice);
                            // Send updated menu to all - since price update
                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            try {
                                client.sendToClient(menuResponse);
                                sendToAllClients(menuResponse);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //delete menuChanges (if approved / or not) , we need to delete anyway.
                        DatabaseServer.deleteMenuChange(menuChangeID);
                        try {
                            Warning successMsg = new Warning("Successfully added menuChange!");
                            client.sendToClient(successMsg);
                            //Sending everyone the updatedMenuChanges.
                            List<MenuChangeEnt> menuChanges = DatabaseServer.getMenuChanges();
                            Message menuResponse = new Message("menuChange", new Object[]{menuChanges});
                            client.sendToClient(menuResponse);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        try {
                            Warning failMsg = new Warning("Failed to set MenuChanges, wrong params!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                // -----------------------------------------------------------
                // UpdatePrice
                // Expecting payload: [int dishId, int newPrice]
                // -----------------------------------------------------------
                case "UpdatePrice": {
                    if (payload.length == 4) {
                        int id = (int) payload[0];
                        double price = (double) payload[1];
                        boolean isSalePrice = (boolean) payload[2];
                        double salePrice = (double) payload[3];

                        try {
                            DatabaseServer.updatePriceForDish(id, price, isSalePrice, salePrice);

                            // Send updated menu to all
                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            client.sendToClient(menuResponse);
                            sendToAllClients(menuResponse);

                            // Also notify the requesting client
                            Warning successMsg = new Warning("Successfully updated price!");
                            client.sendToClient(successMsg);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "UpdateBranch": {
                    if (payload.length == 2) {
                        int id = (int) payload[0];
                        int branchId = (int) payload[1];
                        try {
                            DatabaseServer.updateBranchForDish(id, branchId);

                            // Send updated menu to all
                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            client.sendToClient(menuResponse);
                            sendToAllClients(menuResponse);

                            // Also notify the requesting client
                            Warning successMsg = new Warning("Successfully updated branch!");
                            client.sendToClient(successMsg);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "removeDish": {
                    if (payload.length == 1) {
                        int id = (int) payload[0];
                        try {
                            DatabaseServer.deleteDish(id);

                            // Send updated menu to all
                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            client.sendToClient(menuResponse);
                            sendToAllClients(menuResponse);

                            // Also notify the requesting client
                            Warning successMsg = new Warning("Successfully deleted dish!");
                            client.sendToClient(successMsg);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "add dish": {
                    if (payload.length == 9) {
                        String name = (String) payload[0];
                        String description = (String) payload[1];
                        int branchID = (int) payload[2];
                        List<String> ingredients = (List<String>) payload[3];
                        List<String> toppings = (List<String>) payload[4];
                        String image = (String) payload[5];
                        double price = (double) payload[6];
                        boolean isSalePrice = (boolean) payload[7];
                        double salePrice = (double) payload[8];

                        Dish newDish = new Dish(price, name, description, branchID, ingredients,toppings, image, salePrice, isSalePrice);

                        try {
                            Boolean result = DatabaseServer.addDish(newDish);

                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            client.sendToClient(menuResponse);
                            sendToAllClients(menuResponse);

                            if (result) {
                                Warning successMsg = new Warning("Successfully added dish!");
                                client.sendToClient(successMsg);
                            } else {
                                Warning failMsg = new Warning("Failed to add dish!");
                                client.sendToClient(failMsg);
                            }

                        } catch (IOException e) {
                            System.err.println("IOException: " + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.err.println("Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Warning failMsg = new Warning("Failed to add dish!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "update dish": {
                    // Expect payload in the order:
                    // [int dishId, String name, String description,
                    //  int branchID, List<String> ingredients, String image, int price]

                    if (payload.length == 10) {
                        int dishId = (int) payload[0];
                        String name = (String) payload[1];
                        String desc = (String) payload[2];
                        int branchID = (int) payload[3];
                        List<String> ingr = (List<String>) payload[4];
                        List<String> toppings = (List<String>) payload[5];
                        String image = (String) payload[6];
                        double price = (double) payload[7];
                        boolean isSalePrice = (boolean) payload[8];
                        double salePrice = (double) payload[9];

                        Dish dishToUpdate = new Dish(price, name, desc, branchID, ingr,toppings, image, salePrice, isSalePrice);
                        dishToUpdate.setId(dishId);

                        // Wrap the updateDish call in a broad try/catch
                        try {
                            boolean result = DatabaseServer.updateDish(dishToUpdate);

                            // Refresh the menu for all clients
                            List<DishEnt> dishes = DatabaseServer.getMenu();
                            List<BranchEnt> branches = DatabaseServer.getBranches();
                            Message menuResponse = new Message("menuResponse", new Object[]{dishes, branches});
                            client.sendToClient(menuResponse);
                            sendToAllClients(menuResponse);

                            // Notify the requesting client
                            if (result) {
                                Warning successMsg = new Warning("Successfully updated dish!");
                                client.sendToClient(successMsg);
                            } else {
                                Warning failMsg = new Warning("Failed to update dish!");
                                client.sendToClient(failMsg);
                            }
                        } catch (Exception ex) {
                            System.err.println("Exception thrown while updating dish!");
                            ex.printStackTrace();
                            // Optionally, send a fail message to client
                            try {
                                Warning failMsg = new Warning("Error: " + ex.getMessage());
                                client.sendToClient(failMsg);
                            } catch (IOException ioEx) {
                                ioEx.printStackTrace();
                            }
                        }

                    } else {
                        try {
                            Warning failMsg = new Warning("Failed to update dish!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "add order": {
                    if (payload.length == 14) {
                        try {
                            //List<Integer> dishIds, List<String> adaptaions, String orderType, int selectedBranch, Date orderDate, Double finalPrice, String name, String address, String phone, String userId, String cardNumber, int month, int year, String cvv
                            List<Integer> dishIds = (List<Integer>) payload[0];
                            List<String> adaptations = (List<String>) payload[1];
                            String orderType = (String) payload[2];
                            int selectedBranch = (int) payload[3];
                            Date orderDate = (Date) payload[4];
                            double finalPrice = (double) payload[5];
                            String name = (String) payload[6];
                            String address = (String) payload[7];
                            String phone = (String) payload[8];
                            String userId = (String) payload[9];
                            String cardNum = (String) payload[10];
                            int cardMonth = (int) payload[11];
                            int cardYear = (int) payload[12];
                            String cvv = (String) payload[13];


                            // Determine if the order is for delivery
                            boolean isDelivery = orderType.equalsIgnoreCase("delivery");

                            // Create BuyerDetails with buyer's info.
                            // (Assuming BuyerDetails has a matching constructor.)
                            BuyerDetails buyerDetails = new BuyerDetails(name, address, phone, userId, cardNum, cardMonth, cardYear, cvv);

                            // Create a new Order using the existing constructor
                            Order newOrder = new Order(selectedBranch, isDelivery, dishIds, adaptations, buyerDetails, orderDate, finalPrice);
                            // Set the additional order information


                            // Save the order to the database
                            int orderId = DatabaseServer.addOrder(newOrder);
                            if (orderId != -1) {
//                                Warning successMsg = new Warning("Order added successfully!");
//                                client.sendToClient(successMsg);

                                Message response = new Message("orderResponse", new Object[]{"add", orderId});
                                client.sendToClient(response);
                            } else {
                                Warning failMsg = new Warning("Failed to add order!");
                                client.sendToClient(failMsg);
                            }

                        } catch (Exception e) {
                            try {
                                Warning failMsg = new Warning("Error, Failed to add order: " + e.getMessage());
                                client.sendToClient(failMsg);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "cancel order": {
                    if (payload.length == 2) {
                        try {
                            int orderId = (int) payload[0];
                            String phoneNumber = (String) payload[1];


                            // cancel the order in the database (update status, we don't want to remove it from the database completely).
                            Object[] results = DatabaseServer.cancelOrder(orderId, phoneNumber);
                            if ((int) results[0] != -1) {
//                                Warning successMsg = new Warning("Order canceled successfully!");
//                                client.sendToClient(successMsg);

                                Message response = new Message("orderResponse", new Object[]{"cancel", results[0],results[1]});
                                client.sendToClient(response);
                            } else {
                                Warning failMsg = new Warning("Failed to cancel order!");
                                client.sendToClient(failMsg);
                            }

                        } catch (Exception e) {
                            try {
                                Warning failMsg = new Warning("Error, Failed to cancel order: " + e.getMessage());
                                client.sendToClient(failMsg);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "cancel table order": {
                    if (payload.length == 2) {
                        try {
                            int orderId = (int) payload[0];
                            String phoneNumber = (String) payload[1];

                            // cancel the order in the database (update status, we don't want to remove it from the database completely).
                            int status = DatabaseServer.cancelTableOrder(orderId, phoneNumber);

                            if (status != -1) {
                                Message response = new Message("cancel table order response", new Object[]{status});
                                client.sendToClient(response);
                                // update map
                                response = getCurrentAvailableTablesInBranch(DatabaseServer.getBranchIdFromTableOrderId(orderId));
                                sendToAllClients(response);
                            } else {
                                Warning failMsg = new Warning("Failed to cancel order!");
                                client.sendToClient(failMsg);
                            }

                        } catch (Exception e) {
                            try {
                                Warning failMsg = new Warning("Error, Failed to cancel order: " + e.getMessage());
                                client.sendToClient(failMsg);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "check tables": {
                    if (payload.length == 5) {
                        try {
                            int branchId = (int) payload[0];
                            String date = (String) payload[1];
                            String time = (String) payload[2];
                            int numberOfGuests = (int) payload[3];
                            String location = (String) payload[4];

                            // print to console
                            System.out.println("Received table check request: Branch=" + branchId + ", Date=" + date + ", Time=" + time + ", Guests=" + numberOfGuests + ", Location=" + location);

                            List<Integer> availableTablesIds = DatabaseServer.checkAvailableTables(branchId, date, time, numberOfGuests, location, false);
                            if(availableTablesIds != null) System.out.println("availableTablesIds: " + availableTablesIds);

                            // Send response back to client
                            Message response = new Message("availableTables response", new Object[]{availableTablesIds});
                            client.sendToClient(response);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case "add table order":{
                    if (payload.length == 14) {
                        try{
                            List<Integer> availableTablesIds = (List<Integer>) payload[0];
                            int branchId = (int) payload[1];
                            String date = (String) payload[2];
                            String time = (String) payload[3];
                            int numberOfGuests = (int) payload[4];
                            String location = (String) payload[5];

                            String name = (String) payload[6];
                            String address = (String) payload[7];
                            String phone = (String) payload[8];
                            String userId = (String) payload[9];
                            String cardNum = (String) payload[10];
                            int cardMonth = (int) payload[11];
                            int cardYear = (int) payload[12];
                            String cvv = (String) payload[13];

                            // needed if is ordered online or in place by the host
                            boolean buyerDetailsNeeded = !name.equals("-");
                            BuyerDetails buyerDetails = new BuyerDetails(name, address, phone, userId, cardNum, cardMonth, cardYear, cvv); // Create BuyerDetails with buyer's info.

                            // if for some reason input didn't pass right
                            if (availableTablesIds == null || availableTablesIds.isEmpty()) {
                                System.out.println("SimpleServer: availableTablesIds is null or empty.");
                                throw new Exception("Pleas try again.");
                            }

                            // get the tables by their ids
                            List<TableSchema> tables= DatabaseServer.getTablesWithIds(availableTablesIds);

                            // if there was an error retrieving the tables
                            if (tables == null || tables.isEmpty()) {
                                System.out.println("SimpleServer: tables list is null or empty.");
                                throw new Exception("Pleas try again.");
                            }

                            TableOrder tableOrder= new TableOrder(branchId, tables, date, time, numberOfGuests, location, 0, buyerDetailsNeeded, buyerDetails);
                            int orderId = DatabaseServer.addTableOrder(tableOrder); // return the id of the new order. -1 if failed, -2 if the tables were taken by other client

                            if (orderId != -1 && orderId != -2) { // success
                                System.out.println("Added table order with id: " + orderId);
                                Message response = new Message("TableOrderResponse", new Object[]{orderId});
                                client.sendToClient(response);
                                //update map at client
                                response = getCurrentAvailableTablesInBranch(branchId);
                                sendToAllClients(response);

                            } else if(orderId == -1){  // fail to save to database
                                Warning failMsg = new Warning("Failed to add order! Please try again.");
                                client.sendToClient(failMsg);
                            }
                            else {  // orderId=-2  there's no more room
                                Message response = new Message("Tables were stolen", new Object[]{});
                                client.sendToClient(response);
                            }

                        } catch (Exception e) {
                            try {
                                Warning failMsg = new Warning("Error, Failed to add order: " + e.getMessage());
                                client.sendToClient(failMsg);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                case "get tables for map": {
                    if (payload.length == 1) {
                        try {
                            int branchId = (int) payload[0];
                            System.out.println("Received table check request: Branch=" + branchId + ", Date=" + payload[0]);
                            Message response = getCurrentAvailableTablesInBranch(branchId);
                            client.sendToClient(response);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                // -----------------------------------------------------------
                // login
                // Expecting payload: [String email, String password]
                // -----------------------------------------------------------
                case "login": {

                    if (payload.length == 2) {
                        String email = (String) payload[0];
                        String password = (String) payload[1];
                        try {
                            Object[] loginResult = DatabaseServer.userLogin(email, password);
                            boolean loginSuccess = (boolean) loginResult[0];
                            if (loginSuccess) {

                                int workerId = (int) loginResult[1];
                                int ruleId = (int) loginResult[2];
                                List<Integer> userBranchesIds = (List<Integer>) loginResult[3];

                                Message response = new Message("loginResponse", new Object[]{true, workerId, ruleId, userBranchesIds});
                                client.sendToClient(response);

                            } else {
                                System.out.println("Login failed. Invalid credentials or already logged in.");
                                Message response = new Message("loginResponse", new Object[]{false, -1});
                                client.sendToClient(response);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;
                }

                // -----------------------------------------------------------
                // logout
                // Expecting payload: [int workerId]
                // -----------------------------------------------------------
                case "logout": {
                    if (payload.length == 2) {
                        int userId = (int) payload[0];
                        boolean updateStatus = (boolean) payload[1];
                        try {
                            Object[] logoutResult = DatabaseServer.userLogout(userId);
                            boolean logoutSuccess = (boolean) logoutResult[0];

                            if (updateStatus) { //Update Status - in case we close the app window, auto logout.
                                if (logoutSuccess) {
                                    System.out.println("Successful logout for Worker ID: " + userId);
                                    Message response = new Message("logoutResponse", new Object[]{true, userId});
                                    client.sendToClient(response);
                                } else {
                                    System.out.println("Logout failed (either not found or already logged out).");
                                    Message response = new Message("logoutResponse", new Object[]{false, -1});
                                    client.sendToClient(response);
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                // -----------------------------------------------------------
                // complaints commands
                // -----------------------------------------------------------
                // add complaint to the db
                case "complaint": {
                    if (payload.length == 12) {
                        String complaintText = (String) payload[0];
                        Date date = (Date) payload[1];
                        int branchID = (int) payload[2];
                        String name = (String) payload[3];
                        String address = (String) payload[4];
                        String phone = (String) payload[5];
                        String userId = (String) payload[6];
                        String cardNum = (String) payload[7];
                        int cardMonth = (int) payload[8];
                        int cardYear = (int) payload[9];
                        String cvv = (String) payload[10];
                        String email = (String) payload[11];

                        Complaint newComplaint = new Complaint(complaintText, date, branchID, new BuyerDetails(name, address, phone, userId, cardNum, cardMonth, cardYear, cvv), email);
                        try {
                            boolean result = DatabaseServer.addComplaint(newComplaint);

                            if (result) {
                                Warning successMsg = new Warning("Complaint successfully added!");
                                client.sendToClient(successMsg);

                                // send new complaint to clients to update open handle tables
                                List<ComplaintEnt> complaintsList = new ArrayList<>(); // Initialize the list
                                ComplaintEnt newComplaintENT = Convertor.convertToComplaintEnt(newComplaint); // Create a new complaint
                                complaintsList.add(newComplaintENT); // Add the complaint to the list
                                Message response = new Message("complaints response", new Object[]{complaintsList});
                                sendToAllClients(response);
                            } else {
                                Warning failMsg = new Warning("Failed to add Complaint!");
                                client.sendToClient(failMsg);
                            }
                        } catch (Exception e) {
                            System.err.println("Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Warning failMsg = new Warning("Failed to add Complaint!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;

                }
                // send all the active complaints in the db in complaintEnt list
                case "get complaints": {
                    try {
                        List<ComplaintEnt> complaints = DatabaseServer.getActiveComplaints();
                        client.sendToClient(new Message("complaints response", new Object[]{complaints}));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "handle complaint": {
                    if (payload.length == 2) {
                        int complaintId = (int) payload[0];
                        double refund = (double) payload[1];
                        DatabaseServer.handleComplaint(complaintId, refund);

                        // send new complaint to clients to update open handle tables
                        List<ComplaintEnt> complaintsList = new ArrayList<>(); // Initialize the list
                        ComplaintEnt newComplaintENT = new ComplaintEnt(complaintId, "", null, "", 0, "", 0); // Create a new complaint with the removed complaint id to remove it from open tables
                        complaintsList.add(newComplaintENT); // Add the complaint to the list
                        sendToAllClients(new Message("complaints response", new Object[]{complaintsList}));
                    }
                }

                case "get branch report":{
                    if (payload.length == 3) {
                        int year = (int) payload[0];
                        int month = (int) payload[1];
                        int branchId = (int) payload[2];
                        try {
                            BranchReportEnt branchReport = DatabaseServer.generateBranchReport(branchId, year, month);
                            Message response = new Message("branch report response", new Object[]{branchReport});
                            client.sendToClient(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    }

                }

                // -----------------------------------------------------------
                // Unrecognized command
                // -----------------------------------------------------------
                default:
                    System.out.println("Unknown command received: " + command);
                    break;
            }

        } else {
            // If msg is not an OCSFMessage, handle your old logic or ignore
            System.out.println("Received unknown message type: " + msg);
        }
    }



    // send message for all the clients
    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                System.out.println(message.getCommand());
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    // prepare a message for restaurant map of branch id (longest function name in the code?)
    private Message getCurrentAvailableTablesInBranch(int branchId) throws Exception {

        List<TableSchema> availableTables= DatabaseServer.getTablesForMap(branchId); // get available tables in branch
        List<TableSchema> allTables= DatabaseServer.getAllBranchTables(branchId); // get all tables in branch

        // get taken tables (this might look inefficient but keep in mind we only have like 20 tables in total so It's really nothing)
        boolean availabe = false;
        List<TableSchema> takenTables = new ArrayList<>();
        for (TableSchema table : allTables) {
            for(TableSchema availableTable : availableTables) {
                if (availableTable.getTableId() == table.getTableId()) {
                    availabe = true;
                    break;
                }
            }
            if(!availabe){
                takenTables.add(table);
            }
            availabe = false;
        }

        // convert tables to entities
        List<TableEnt> availableTablesEnt= Convertor.convertToTableEntList(availableTables);
        List<TableEnt> takenTablesEnt= Convertor.convertToTableEntList(takenTables);

        // Send response back to client
        Message response = new Message("TablesForMapResponse", new Object[]{availableTablesEnt, takenTablesEnt, branchId});
        System.out.println("tables for branch id: " + branchId);
        return response;
    }

    // check complaints periodically to auto handle complaints older than 24 hours
    private void startChecksScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            boolean complaintsUpdated = DatabaseServer.autoHandleOldComplaints();

            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1, Saturday = 7

            // Adjust day index to match openingHours list (0 = Monday, 6 = Sunday)
            int openingHoursIndex = (dayOfWeek + 5) % 7;

            if (minute % 15 == 0) { // Check every round 15 minuets: 0, 15,30,45 of the hour
                try {

                    // go over all of the branches
                    for (int branchId = 1; branchId <= maxBranchId; branchId++) {
                        List<String> openingHours = DatabaseServer.getBranchOpeningHours(branchId);

                        if (openingHours == null || openingHours.size() <= openingHoursIndex) continue;

                        String hoursToday = openingHours.get(openingHoursIndex);
                        if (hoursToday == null || hoursToday.equalsIgnoreCase("Closed")) continue;

                        // Parse opening and closing times
                        String[] parts = hoursToday.split("-");
                        if (parts.length != 2) continue;

                        int openHour = Integer.parseInt(parts[0].split(":")[0]);
                        int openMinute = Integer.parseInt(parts[0].split(":")[1]);
                        int closeHour = Integer.parseInt(parts[1].split(":")[0]);
                        int closeMinute = Integer.parseInt(parts[1].split(":")[1]);

                        Calendar openTime = (Calendar) calendar.clone();
                        openTime.set(Calendar.HOUR_OF_DAY, openHour);
                        openTime.set(Calendar.MINUTE, openMinute);

                        Calendar closeTime = (Calendar) calendar.clone();
                        closeTime.set(Calendar.HOUR_OF_DAY, closeHour);
                        closeTime.set(Calendar.MINUTE, closeMinute);

                        // Run check only if current time is within opening hours
                        if (calendar.after(openTime) && calendar.before(closeTime)) {
                            Message response = getCurrentAvailableTablesInBranch(branchId);
                            sendToAllClients(response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (complaintsUpdated) {
                System.out.println("Auto-handled complaints. Notifying clients...");
            }
        }, 0, schedulerIntervals, TimeUnit.MINUTES);
    }

}
