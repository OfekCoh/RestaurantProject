package il.cshaifasweng.OCSFMediatorExample.server;


import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private DatabaseServer databaseServer;

    public SimpleServer(int port, String databasePassword) {
        super(port);
        DatabaseServer.password = databasePassword;
        // create the database
        databaseServer = new DatabaseServer(databasePassword);
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
                    if (payload.length == 8) {
                        String name = (String) payload[0];
                        String description = (String) payload[1];
                        int branchID = (int) payload[2];
                        List<String> ingredients = (List<String>) payload[3];
                        String image = (String) payload[4];
                        double price = (double) payload[5];
                        boolean isSalePrice = (boolean) payload[6];
                        double salePrice = (double) payload[7];

                        Dish newDish = new Dish(price, name, description, branchID, ingredients, image, salePrice, isSalePrice);

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

                    if (payload.length == 9) {
                        int dishId = (int) payload[0];
                        String name = (String) payload[1];
                        String desc = (String) payload[2];
                        int branchID = (int) payload[3];
                        List<String> ingr = (List<String>) payload[4];
                        String image = (String) payload[5];
                        double price = (double) payload[6];
                        boolean isSalePrice = (boolean) payload[7];
                        double salePrice = (double) payload[8];

                        Dish dishToUpdate = new Dish(price, name, desc, branchID, ingr, image, salePrice, isSalePrice);
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
                case "add order":{
                    if (payload.length == 14) {
                        try{
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
                            Order newOrder = new Order(selectedBranch, isDelivery, dishIds, adaptations, buyerDetails,orderDate,finalPrice);
                            // Set the additional order information


                            // Save the order to the database
                            int orderId = DatabaseServer.addOrder(newOrder);
                            if (orderId != -1) {
//                                Warning successMsg = new Warning("Order added successfully!");
//                                client.sendToClient(successMsg);

                                Message response = new Message("orderResponse", new Object[]{orderId});
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
//                                System.out.println("Welcome Worker ID: " + workerId);
                                Message response = new Message("loginResponse", new Object[]{true, workerId, ruleId});
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
                    if (payload.length == 1) {
                        int userId = (int) payload[0];
                        try {
                            Object[] logoutResult = DatabaseServer.userLogout(userId);
                            boolean logoutSuccess = (boolean) logoutResult[0];

                            if (logoutSuccess) {
                                System.out.println("Successful logout for Worker ID: " + userId);
                                Message response = new Message("logoutResponse", new Object[]{true, userId});
                                client.sendToClient(response);
                            } else {
                                System.out.println("Logout failed (either not found or already logged out).");
                                Message response = new Message("logoutResponse", new Object[]{false, -1});
                                client.sendToClient(response);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }

                // -----------------------------------------------------------
                // complain command
                // -----------------------------------------------------------
                case "complaint": {
                    if (payload.length == 4) {
                        String complaintText = (String) payload[0];
                        Date date = (Date) payload[1];
                        String name = (String) payload[2];
                        String creditCardNumber = (String) payload[3];

                        Complaint newComplaint = new Complaint(complaintText, date, creditCardNumber, name);

                        try {
                            boolean result = DatabaseServer.addComplaint(newComplaint);

                            if (result) {
                                Warning successMsg = new Warning("Complaint successfully added!");
                                client.sendToClient(successMsg);
                            } else {
                                Warning failMsg = new Warning("Failed to add Complaint!");
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
                            Warning failMsg = new Warning("Failed to add Complaint!");
                            client.sendToClient(failMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;

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

    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
