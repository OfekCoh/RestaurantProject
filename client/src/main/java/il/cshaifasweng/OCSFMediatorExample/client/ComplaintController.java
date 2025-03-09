/**
 * Sample Skeleton for 'Complaint.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Date;

public class ComplaintController {

    @FXML // fx:id="complaintText"
    private TextField complaintText; // Value injected by FXMLLoader

    @FXML // fx:id="CardDetails"
    private TextField cardDetails; // Value injected by FXMLLoader

    @FXML // fx:id="complainerDetails"
    private TextField complainerDetails; // Value injected by FXMLLoader


    @FXML // fx:id="submitButton"
    private Button submitButton; // Value injected by FXMLLoader

    @FXML
    void submitComplaint(ActionEvent event) throws IOException {
        // if all of the details are valid
        if (!complainerDetails.getText().isEmpty() && !complaintText.getText().isEmpty() && isValidCreditCardFormat(complainerDetails.getText())) {
            SimpleClient.getClient().sendComplaint(complaintText.getText(), new Date(), complainerDetails.getText(), cardDetails.getText());// buyer details
            App.setRoot("primary");
        }
        if(complainerDetails.getText().isEmpty()){

        }
        if(complaintText.getText().isEmpty()){

        }
    }
    // check if the credit card is an actually credit card number or empty
    private boolean isValidCreditCardFormat (String input){
        return input.isEmpty() || input.matches("\\d{4} \\d{4} \\d{4} \\d{4}");
    }

}
