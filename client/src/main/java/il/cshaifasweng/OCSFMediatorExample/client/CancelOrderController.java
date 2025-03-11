package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

import java.io.IOException;

public class CancelOrderController {
    private static String orderType;

    public static void setOrderType(String orderType) {
        CancelOrderController.orderType = orderType;
    }

    @FXML
    private Label titleLabel;

    @FXML
    private TextField orderIdField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;

    /**
     * This method is automatically called when the FXML is loaded.
     * You can do any necessary initialization here.
     */
    @FXML
    public void initialize() {
        //Enable the cancelButton (in case we cancel several orders).
//        cancelButton.setDisable(false);
        if (orderType.equalsIgnoreCase("table")) {
            titleLabel.setText("Cancel Table Order:");
        }
    }

    /**
     * This method is called when the user clicks the "Cancel Order" button.
     */
    @FXML
    private void handleCancelOrderAction(ActionEvent event) {
        errorLabel.setText(""); // clear previous errors

        // 1) Validate the Order ID (must be a positive integer)
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdField.getText().trim());
            if (orderId <= 0) {
                errorLabel.setText("Order ID must be a positive integer!");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Order ID must be a valid integer!");
            return;
        }

        // 2) Validate the phone number
        String phoneNumber = phoneNumberField.getText().trim();
        if (phoneNumber.isEmpty()) {
            errorLabel.setText("Phone number cannot be empty!");
            return;
        }

        // 3) Send the "cancel order" request to the server
        try {
            if (orderType.equalsIgnoreCase("order")) {
                //Regular Table
                SimpleClient.getClient().sendCancelOrder(orderId, phoneNumber);
                System.out.println("Sending 'cancel order' request: orderId=" + orderId + ", phone=" + phoneNumber);
            } else if (orderType.equalsIgnoreCase("table")) {
                //Table Order
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error sending cancel request: " + e.getMessage());
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            App.setRoot("orderSelectionType");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}