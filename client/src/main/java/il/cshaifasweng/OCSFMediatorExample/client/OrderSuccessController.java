package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;

public class OrderSuccessController {
    private static int selectedOrderID;

    @FXML
    private Button backButton;

    @FXML
    private Label orderLabel;

    public static void setOrderID(int orderID) {
        selectedOrderID = orderID;
    }

    @FXML
    void initialize() {
        orderLabel.setText("Order ID: " + selectedOrderID);
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
