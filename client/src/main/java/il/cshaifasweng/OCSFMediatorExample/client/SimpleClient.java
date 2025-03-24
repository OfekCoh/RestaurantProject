package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SimpleClient extends AbstractClient {

    public static SimpleClient client = null;
    public static int port = 3000;
    public static String ip = "localhost";
    public static List<DishEnt> DishList;
    public static List<BranchEnt> BranchList;
    public static int userID = -1;
    public static int ruleID = -1;
    public static List<Integer> userBranchesIdList = new ArrayList<Integer>();// the branches the user works at

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
//        System.out.println("UserID: " + userID + " RuleID: " + ruleID);

        // 1) Check if we got an OCSFMessage
        if (msg instanceof Message) {
            Message message = (Message) msg;
            String command = message.getCommand();
            Object[] payload = message.getPayload();

            switch (command) {
                case "loginResponse": {
                    // payload[0] = boolean success
                    // payload[1] = int workerId
                    boolean success = (boolean) payload[0];
                    if (success) {
                        int workerId = (int) payload[1];
                        int rule = (int) payload[2];
                        List<Integer> userBranchesId = (List<Integer>) payload[3];
                        //Setting the local rules.
                        userID = workerId;
                        ruleID = rule;
                        if(ruleID == 4){// CEO has access to all of the branches
                            userBranchesIdList = BranchList.stream() .map(BranchEnt::getId).toList(); // get all branches id to userbranchesID

                        }else{
                            userBranchesIdList = userBranchesId;
                        }

                        System.out.println("Client: Login success! Worker ID = " + workerId + " Rule ID = " + rule);
                        EventBus.getDefault().post(new WarningEvent(new Warning("Login Success!")));
                        EventBus.getDefault().post(new LoginEvent(workerId, rule));

                        /*
                        TODO - Move panel afterwards, and not display the login anymore
                         */
                    } else {
                        System.out.println("Client: Login failed!");
                        EventBus.getDefault().post(new WarningEvent(new Warning("login failed, either wrong password/email or user already logged in!")));
                    }
                    break;
                }

                case "logoutResponse": {
                    boolean success = (boolean) payload[0];
                    int userId = (int) payload[1];

                    if (success) {
                        System.out.println("Client: Logout success for Worker ID = " + userId);

                    } else {
                        System.out.println("Client: Logout failed or user already logged out!");
                        EventBus.getDefault().post(new WarningEvent(new Warning("Client: Logout failed or user already logged out!")));
                    }
                    break;
                }

                case "menuResponse": {
                    System.out.println("Client: Menu success!");
                    //TODO RETURN also branches in payload 0. also return array list of list dish for each branch.
                    // The server put a List<Dish> in payload[0]
                    List<DishEnt> dishes = (List<DishEnt>) payload[0];
                    List<BranchEnt> branches = List.of();
                    int dishSize = dishes.size();
                    if (payload[1] != null) {
                        branches = (List<BranchEnt>) payload[1];
                    }

                    if (dishes != null) {
                        for (DishEnt dish : dishes) {
                            System.out.println("Dish: " + dish);
                        }
                    }

                    if (branches != null) {
                        System.out.println("Branches Print");
                        for (BranchEnt branch : branches) {
                            System.out.println("Branch: " + branch);
                        }
                    } else {
                        System.out.println("Branches list is NULL!");
                    }

                    // Now do something with these dishes
                    System.out.println("Received menuResponse with " + dishes.size() + " dishes.");
                    DishList = dishes;
                    BranchList = branches;
                    EventBus.getDefault().post(new DishEvent(DishList));
                    EventBus.getDefault().post(new BranchEvent(BranchList));
                    break;
                }

                case "menuChange": {
                    List<MenuChangeEnt> menuChangeList = (List<MenuChangeEnt>) payload[0];
                    System.out.println("Received menuChange with " + menuChangeList.size() + " dishes.");
                    EventBus.getDefault().post(new MenuChangeEvent(menuChangeList));
                    break;
                }

                case "orderResponse": {
                    System.out.println("Client: Order success!");
                    System.out.println("Received orderResponse with " + payload[0] + " ID.");
                    Platform.runLater(() -> {
                        try {
                            OrderStatusController.setType((String) payload[0]);
                            OrderStatusController.setOrderID((int) payload[1]);
                            if(((String) payload[0]).equalsIgnoreCase("cancel"))
                            {
                                OrderStatusController.setRefundAmount((double) payload[2]);
                            }else{
                                OrderStatusController.setRefundAmount(0);
                            }
                            App.setRoot("orderStatus");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                }

                case "cancel table order response": {
                    Platform.runLater(() -> {
                        try {
                            int status= (int) payload[0];
                            if(status==1){  // free cancellation success
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, ("order was canceled successfully, and no charges were made."));
                                alert.show();
                            }
                            else if(status==2){ // paid cancellation success
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, ("Order canceled successfully.\nA ₪10 charge applies due to last-hour cancellation."));
                                alert.show();
                            }
                            else { // -1 error
                                Alert alert = new Alert(Alert.AlertType.ERROR, ("Something went wrong! Please try again."));
                                alert.show();
                            }
                            App.setRoot("primary");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                }

                case "TableOrderResponse": {
                    System.out.println("Order success! order id is: " + payload[0]);
                    Platform.runLater(() -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, ("Date: " + TableOrderManage.getDate() + " at " + TableOrderManage.getTime()) +
                                    "\n\nA reservation lasts for 90 minutes or until closing time (whichever comes first)." +
                                    "\n\nPlease note: order number required for cancellation" +
                                    "\n\nSee you then!");
                            alert.setHeaderText("Order #" + payload[0] + " Completed!");
                            alert.setTitle("Order Info");
                            alert.show();
                            TableOrderManage.resetFields();
                            App.setRoot("primary");

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                }

                case "TablesForMapResponse": {
                    // this return tables available and tables taken at the current time
                    List<TableEnt> availableTables= (List<TableEnt>) payload[0];
                    List<TableEnt> takenTables= (List<TableEnt>) payload[1];
                    int branchId = (int) payload[2];
                    System.out.println("Received Tables For Map Response");
                    EventBus.getDefault().post(new RestaurantMapEvent(takenTables, availableTables, branchId));

                }

                case "complaints response": {
                    List<ComplaintEnt> complaints = (List<ComplaintEnt>) payload[0];
                    EventBus.getDefault().post(new ComplaintEvent(complaints));
                    break;
                }

                case "branch report response":{
                    BranchReportEnt branchReport = (BranchReportEnt) payload[0];
                    EventBus.getDefault().post(new BranchReportEvent(branchReport));
                    System.out.println("received branch report ");
                    break;
                }

                case "availableTables response": {
                    List<Integer> availableTablesIds = (List<Integer>) payload[0];

                    if(availableTablesIds == null) {  // must've been an error. try again
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, ("Something went wrong! Please try again."));
                            alert.show();
                        });
                        break;
                    }

                    else if(availableTablesIds.isEmpty()) {  // no seats at this time
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please try a different time or branch.");
                            alert.setHeaderText("There are no free tables!");
                            alert.show();
                        });
                        break;
                    }

                    // there are seats
                    System.out.println("SimpleClient.response: availableTablesIds = " + availableTablesIds);
                    TableOrderManage.setAvailableTablesIds(availableTablesIds); // add tables to the order

                    try {
                        if(SimpleClient.userID != -1) {   // it's a worker

                            // add this order to the database. buyer details don't matter
                            sendAddTableOrder("-", "-", "-", "-", "-", -1, -1, "-");
                        }
                        else {  // it's a costumer
                            BuyerDetailsFormController.setCallerType("orderTable");  // Pass caller "cart" to buyerform

                            Platform.runLater(() -> {
                                try {
                                    App.setRoot("buyerForm");  // Switch to the buyer form scene
                                } catch (IOException e) {
                                    e.printStackTrace();  // Handle potential IOException if App.setRoot throws an exception
                                }
                            });
                        }
                    }
                    catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, ("Something went wrong! Please try again."));
                        alert.show();
                    }
                    break;
                }

                case "Tables were stolen": {
                    System.out.println("Tables were stolen");
                    Platform.runLater(() -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.ERROR, ("Please try a different time or branch."));
                            alert.setHeaderText("There are no free tables!");
                            alert.show();
                            TableOrderManage.resetFields();
                            App.setRoot("orderTable");

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                }

                default:
                    System.out.println("Client: Unknown command from server: " + command);
                    break;
            }
        }

        // 2) Otherwise, check if it's a known entity like MenuEnt, Warning, etc.
//        else if (msg instanceof MenuEnt) {
//            MenuEnt menu = (MenuEnt) msg;
//            DishList = menu.getListDishes();
//            EventBus.getDefault().post(new MenuEvent(menu));
//        }
        else if (msg instanceof Warning) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else {
            System.out.println("Client: Received unknown message type: " + msg);
        }
    }

    // ------------------------------------------------------------------------
    // Helper methods to send OCSFMessage to server
    // ------------------------------------------------------------------------
    public void sendAddClientCommand() throws Exception {
        Message message = new Message("add client", new Object[]{});
        sendToServer(message);
    }

    public void sendGetMenuCommand() throws Exception {
        Message message = new Message("getMenu", new Object[]{});
        sendToServer(message);
    }

    public void sendUpdatePriceCommand(int dishID, double newPrice, boolean isSalePrice, double newSalePrice) throws Exception {
        Message message = new Message("UpdatePrice", new Object[]{dishID, newPrice, isSalePrice, newSalePrice});
        sendToServer(message);
    }

    public void sendAddMenuChange(int dishId, double oldPrice, boolean oldIsOnSale, double oldSalePrice, double newPrice, boolean newIsOnSale, double newSalePrice) throws IOException {
        Message message = new Message("add menuChanges", new Object[]{dishId, oldPrice, oldIsOnSale, oldSalePrice, newPrice, newIsOnSale, newSalePrice});
        sendToServer(message);
    }

    public void sendRemoveDishCommand(int dishID) throws Exception {
        Message message = new Message("removeDish", new Object[]{dishID});
        sendToServer(message);
    }

    public void sendUpdateBranchCommand(int dishID, int newBranchID) {
        try {
            Message message = new Message("UpdateBranch", new Object[]{dishID, newBranchID});
            sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAddDishCommand(DishEnt updatedDish) {
        System.out.println("Client: Add Dish: " + updatedDish);
        try {
            Message message = new Message("add dish", new Object[]{updatedDish.getName(), updatedDish.getDescription(), updatedDish.getBranchID(), Arrays.asList(updatedDish.getIngredients()),Arrays.asList(updatedDish.getToppings()), updatedDish.getImage(), updatedDish.getPrice(), updatedDish.getIsSalePrice(), updatedDish.getSalePrice()});
            sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUpdateDishCommand(DishEnt updatedDish) {
        System.out.println("Client: Update Dish: " + updatedDish);
        try {
            // We'll pack all relevant fields into the payload array
            Message message = new Message("update dish", new Object[]{updatedDish.getId(), updatedDish.getName(), updatedDish.getDescription(), updatedDish.getBranchID(), Arrays.asList(updatedDish.getIngredients()),Arrays.asList(updatedDish.getToppings()), updatedDish.getImage(), updatedDish.getPrice(), updatedDish.getIsSalePrice(), updatedDish.getSalePrice()});
            sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendGetMenuChanges() throws Exception {
        Message message = new Message("get menuChanges", new Object[]{});
        sendToServer(message);
    }

    public void sendSetMenuUpdate(int menuChangeID, int dishID, double newPrice, boolean newIsOnSale, double newSalePrice, boolean status) throws IOException {
        Message message = new Message("set menuChanges", new Object[]{menuChangeID, dishID, newPrice, newIsOnSale, newSalePrice, status});
        sendToServer(message);
    }

    public void sendAddOrder(List<Integer> dishIds, List<String> adaptaions, String orderType, int selectedBranch, Date orderDate, Double finalPrice, String name, String address, String phone, String userId, String cardNumber, int month, int year, String cvv) throws IOException {
        Message message = new Message("add order", new Object[]{dishIds, adaptaions, orderType, selectedBranch, orderDate, finalPrice, name, address, phone, userId, cardNumber, month, year, cvv});
        sendToServer(message);
    }

    public void sendCancelOrder(int orderId, String phoneNum) throws IOException {
        Message message = new Message("cancel order", new Object[]{orderId,phoneNum});
        sendToServer(message);
    }

    public void sendCancelTableOrder(int orderId, String phoneNum) throws IOException {
        Message message = new Message("cancel table order", new Object[]{orderId,phoneNum});
        sendToServer(message);
    }

    public void sendCheckTables() throws IOException {
        Message message = new Message("check tables", new Object[]{
                TableOrderManage.getBranchId(),
                TableOrderManage.getDate(),
                TableOrderManage.getTime(),
                TableOrderManage.getNumberOfGuests(),
                TableOrderManage.getLocation()});
        sendToServer(message);
    }

    public void sendAddTableOrder(String name, String address, String phone, String userID, String cardNumber, Integer month,Integer  year, String cvv) throws IOException {
        Message message = new Message("add table order", new Object[]{
                TableOrderManage.getAvailableTablesIds(),
                TableOrderManage.getBranchId(),
                TableOrderManage.getDate(),
                TableOrderManage.getTime(),
                TableOrderManage.getNumberOfGuests(),
                TableOrderManage.getLocation(),
                name, address, phone, userID, cardNumber, month, year, cvv});
        sendToServer(message);
    }

    public void sendGetTablesForMap(int branchId) throws IOException {
        System.out.println("Client: Get Tables for Map: " + branchId);
        Message message = new Message("get tables for map", new Object[]{branchId});
        sendToServer(message);
    }

    public void sendLoginCommand(String email, String password) throws Exception {
        Message message = new Message("login", new Object[]{email, password});
        sendToServer(message);
    }

    public void sendLogoutCommand(int userId,boolean returnStatus) throws Exception {
        Message message = new Message("logout", new Object[]{userId,returnStatus});
        sendToServer(message);
    }

    public void sendComplaint(String complaintText, Date date, int branchId, String name, String address, String phone, String userID, String cardNumber, Integer month,Integer  year, String cvv, String email) throws IOException {
        Message message = new Message("complaint", new Object[]{complaintText, date, branchId, name, address, phone, userID, cardNumber, month, year, cvv, email});
        sendToServer(message);
    }

    public void sendGetComplaints() throws Exception {
        Message message = new Message("get complaints", new Object[]{});
        sendToServer(message);
    }
    public void sendHandleComplaint(int complaintID, double refund) throws Exception {
        Message message = new Message("handle complaint", new Object[]{complaintID, refund});
        sendToServer(message);
    }

    public void sendGetBranchReport(int year, int month, int branchID) throws Exception {
        Message message = new Message("get branch report", new Object[]{year, month, branchID});
        sendToServer(message);
    }


    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient(ip, port);
        }
        return client;
    }

}
