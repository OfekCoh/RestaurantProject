package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.LoginEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;


public class PrimaryController {
    private static boolean addClientSent = false;
    @FXML
    private Button Branch;

    @FXML
    private Button cancelButton;

    @FXML
    private Button complaint;

    @FXML
    private Button complaintHandleButton;

    @FXML
    private Button login;

    @FXML
    private Button menuView;

    @FXML
    private Button orderDelivery;

    @FXML
    private Button orderTable;

    @FXML
    private Button reportsButton;

    @FXML
    private Button resturantMap;


    //הכנת דוחות
    @FXML
    void makeReport(ActionEvent event) throws IOException {
        App.setRoot("chooseReport");
    }

    @FXML
    void switchToCancelation(ActionEvent event) throws Exception {
        OrderSelectionType.setSelectedMode("cancel");
        App.setRoot("orderSelectionType");
    }


    //טיפול בתלונה
    @FXML
    void switchToComplaint(ActionEvent event) throws IOException {
        App.setRoot("complaint");
    }


    //טיפול בתלונה
    @FXML
    void switchToComplaintHandler(ActionEvent event) throws IOException {
        App.setRoot("handleComplaints");
    }

    @FXML
    void switchToMenu(ActionEvent event) throws Exception {
        SimpleClient.getClient().sendGetMenuCommand();
        BranchSelectionController.setNextWindow("dish selection");
        App.setRoot("branchSelection");
    }

    @FXML
    void switchToLogin(ActionEvent event) throws Exception {
//        SimpleClient.getClient().sendGetMenuCommand();
//        SimpleClient.getClient().sendToServer("getMenu");
        App.setRoot("login");
    }

    @FXML
    private void switchToMenuView(ActionEvent event) throws Exception {
        SimpleClient.getClient().sendGetMenuCommand();
//        SimpleClient.getClient().sendToServer("getMenu");
        App.setRoot("menuUpdate");
//        App.setRoot("addDish");
//        App.setRoot("branchSelection");
    }

    @FXML
    void switchToMenuDelivery(ActionEvent event) throws IOException {
        //Order
        OrderSelectionType.setSelectedMode("order");
        App.setRoot("orderSelectionType");
    }

    @FXML
    void switchToRestaurantMap(ActionEvent event) throws Exception {
        //SimpleClient.getClient().sendGetMenuCommand();
        BranchSelectionController.setNextWindow("restaurant map");
        App.setRoot("branchSelection");
    }

    @FXML
    void switchToTableOrder(ActionEvent event) throws Exception {
        App.setRoot("orderTable");
    }

    @Subscribe
    public void onLoginSuccess(LoginEvent event) {
        Platform.runLater(() -> {
            SimpleClient.userID = event.getUserID();
            SimpleClient.ruleID = event.getRuleID();
            updateButtonVisibility();
        });
    }

    private void updateButtonVisibility() {
        if (login != null) login.setVisible(SimpleClient.userID == -1);

        if (resturantMap != null) resturantMap.setVisible(SimpleClient.userID != -1);
        if (Branch != null) Branch.setVisible(SimpleClient.ruleID >= 3 && SimpleClient.ruleID <= 4);
        if (complaintHandleButton != null)
            complaintHandleButton.setVisible(SimpleClient.ruleID == 1 || SimpleClient.ruleID == 4);
        if (reportsButton != null) reportsButton.setVisible(SimpleClient.ruleID == 2 || SimpleClient.ruleID == 4);
//        if (Menu != null) Menu.setVisible(SimpleClient.ruleID >= 3);
    }

    @FXML
    void initialize() {
        if (!addClientSent) {
            try {
                SimpleClient.getClient().sendAddClientCommand();
                addClientSent = true;
                SimpleClient.getClient().sendGetMenuCommand();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        updateButtonVisibility();
    }
}