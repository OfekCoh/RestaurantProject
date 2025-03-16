package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComplaintController {

    // Static variables to hold complaint details
    private static String complaint;
    private static int branchId;

    public static String getComplainText() {
        return complaint;
    }

    public static int getBranchId() {
        return branchId;
    }

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<BranchEnt> branchComboBox;

    @FXML
    private TextArea complaintText;

    @FXML
    private Button submitButton;

    @FXML
    public void initialize() {
        // Load all branches from Client.BranchList
        List<BranchEnt> branches = SimpleClient.BranchList;

        if (branches != null && !branches.isEmpty()) {
            branchComboBox.setItems(FXCollections.observableArrayList(branches));
        }

        // Set custom cell factory to display "branch id - branch name"
        branchComboBox.setCellFactory(lv -> new ListCell<BranchEnt>() {
            @Override
            protected void updateItem(BranchEnt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getId() + " - " + item.getBranchName());
            }
        });

        // Set button cell to show selected branch properly (after selection)
        branchComboBox.setButtonCell(new ListCell<BranchEnt>() {
            @Override
            protected void updateItem(BranchEnt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getId() + " - " + item.getBranchName());
            }
        });



        // Bind submit button visibility to both conditions: non-empty text & selected branch
        submitButton.visibleProperty().bind(
                branchComboBox.valueProperty().isNotNull()
                        .and(complaintText.textProperty().isNotEmpty())
        );
    }

    @FXML
    void submitComplaint(ActionEvent event) throws IOException {
        // Check if a complaint has been entered
        if (complaintText.getText().isEmpty()) {
            // prints alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Write your complaint!"
                    ));
            alert.show();
            return; // Stop execution
        }

        // Check if a branch is selected
        BranchEnt selectedBranch = branchComboBox.getValue();
        if (selectedBranch == null) {
            // prints alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "choose branch"
                    ));
            alert.show();
            return; // Stop execution
        }

        // Assign values to static variables
        ComplaintController.complaint = complaintText.getText();
        ComplaintController.branchId = selectedBranch.getId(); // Get the branch name

        // Move to buyer form
        BuyerDetailsFormController.setCallerType("complaint");
        App.setRoot("buyerForm");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }
}
