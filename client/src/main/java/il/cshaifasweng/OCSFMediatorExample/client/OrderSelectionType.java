package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class OrderSelectionType {
    private static String selectedMode="";

    public static void setSelectedMode(String mode) {
        selectedMode = mode;
    }

    @FXML
    private Button backButton;

    @FXML
    private Button deliveryButton;

    @FXML
    private Button pickupButton;

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void selectDelivery(ActionEvent event) throws IOException {
        OrderManage.setOrderType("Delivery");
        App.setRoot("selectDeliveryLocation");

    }

    @FXML
    void selectPickup(ActionEvent event) throws IOException {
        OrderManage.setOrderType("Pickup");
        App.setRoot("selectDeliveryLocation");
    }

}
