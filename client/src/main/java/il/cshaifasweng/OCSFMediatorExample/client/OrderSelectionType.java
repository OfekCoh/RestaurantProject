package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class OrderSelectionType {
    private static String selectedMode="";

    public static void setSelectedMode(String mode) {
        selectedMode = mode;
    }

    @FXML
    private Button backButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private Label mainLabel;

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void selectRightButton(ActionEvent event) throws IOException {
        if(selectedMode.equalsIgnoreCase("order")) {
            OrderManage.setOrderType("Delivery");
            App.setRoot("selectDeliveryLocation");
        }else{
            //Cancel Table Order
            CancelOrderController.setOrderType("table");
            App.setRoot("cancelOrder");
        }
    }

    @FXML
    void selectLeftButton(ActionEvent event) throws IOException {
        if(selectedMode.equalsIgnoreCase("order")) {
            OrderManage.setOrderType("Pickup");
            App.setRoot("selectDeliveryLocation");
        }
        else{
            //Cancel Order
            CancelOrderController.setOrderType("order");
            App.setRoot("cancelOrder");
        }
    }

    @FXML
    void initialize() {
       if(!selectedMode.equalsIgnoreCase("order")) { //Not Order
           mainLabel.setText("Select what to cancel");
           leftButton.setText("Cancel Order");
           rightButton.setText("Cancel Table Order");
       }
    }

}
