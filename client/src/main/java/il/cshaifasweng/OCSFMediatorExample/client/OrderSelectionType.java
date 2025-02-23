package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class OrderSelectionType {

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
        App.setRoot("selectDeliveryLocation");
        SelectDeliveryLocationController.setSelectedOrderType("Delivery");
    }

    @FXML
    void selectPickup(ActionEvent event) throws IOException {
        App.setRoot("selectDeliveryLocation");
        SelectDeliveryLocationController.setSelectedOrderType("Pickup");


    }

}
