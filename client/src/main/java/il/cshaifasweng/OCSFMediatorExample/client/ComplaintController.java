/**
 * Sample Skeleton for 'Complaint.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ComplaintController {

    // to access the complaint from the buyers submit form
    private static String complaint;
    public static String getComplainText(){
        return complaint;
    }

    @FXML
    private Button backButton;

    @FXML
    private TextArea complaintText;

    @FXML // fx:id="submitButton"
    private Button submitButton; // Value injected by FXMLLoader


    @FXML
    void submitComplaint(ActionEvent event) throws IOException {
        if(!complaintText.getText().isEmpty()){
            ComplaintController.complaint = complaintText.getText();


            // move to buyers form
            // if its a worker (host) adding the branch it shouldnt go to payment.
            BuyerDetailsFormController.setCallerType("complaint");
            App.setRoot("buyerForm");
        }

    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }


}
