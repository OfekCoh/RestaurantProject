package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class OrderStatusController {
    private static int selectedOrderID;
    private static String type;
    private static double refundAmount;

    @FXML
    private Button backButton;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    public static void setOrderID(int orderID) {
        selectedOrderID = orderID;
    }

    public static void setType(String type) {
        OrderStatusController.type = type;
    }
    public static void setRefundAmount(double amount) {
        OrderStatusController.refundAmount= amount;
    }

    @FXML
    void initialize() {
        //Wrap the text of label3
        label3.setWrapText(true);
        if (type.equalsIgnoreCase("add")) {
            //Order Success, we show the id of the order.
            label2.setText("Order ID: " + selectedOrderID);
        } else if (type.equalsIgnoreCase("cancel")) {
            //Order Cancellation
            label1.setText("Order Cancellation");
            label2.setText("Order Cancelled Successfully!");
            switch (selectedOrderID) {
                case 1:
                    label3.setText("You have received a full refund! refund amount: $" + refundAmount);
                    break;
                case 2:
                    label3.setText("You have received a partial refund! (50%) - Cancellation 1-3 hours before the delivery time , refund amount: $"+ refundAmount);
                    break;
                case 3:
                    label3.setText("You canceled the order less than an hour before the delivery time, so there is no refund.");
                    break;
                default:

            }
        }

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
