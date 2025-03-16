package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.ComplaintEnt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HandleComplaintsController {

    private double refundAmount;

    @FXML
    private Button backButton;

    @FXML
    private TableView<ComplaintEnt> complaintsTable;

    @FXML
    private TableColumn<ComplaintEnt, Integer> idColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> branchNameColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> nameColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> timeRemainingColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> complaintColumn;
    @FXML
    private TableColumn<ComplaintEnt, Void> actionColumn;

    private ObservableList<ComplaintEnt> complaints;

    // Receive complaints from server using EventBus
    @Subscribe
    public void onCompliantEvent(ComplaintEvent event) {
        Platform.runLater(() -> {
            System.out.println("Received ComplaintEvent");

            List<ComplaintEnt> incomingComplaints = event.getComplaints();

            // Remove complaints that are already in the table
            complaints.removeIf(existingComplaint ->
                    incomingComplaints.stream().anyMatch(newComplaint -> newComplaint.getId() == existingComplaint.getId()));

            // Add complaints that are not already in the table
            for (ComplaintEnt complaint : incomingComplaints) {
                if (complaint.getStatus() == 0 && SimpleClient.userBranchesIdList.contains(complaint.getBranchId()) && complaints.stream().noneMatch(existing -> existing.getId() == complaint.getId())) {
                    complaints.add(complaint);
                }
            }
        });
    }

    public void initialize() {
        // register to event bus to receive complaints
        EventBus.getDefault().register(this);
        try {
            SimpleClient.getClient().sendGetComplaints();
            System.out.println("Sent request to get complaints");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // map table columns to ComplaintEnt properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        branchNameColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        complaintColumn.setCellValueFactory(new PropertyValueFactory<>("complaintText"));

        // display the time remaining before 24 hours pass
        timeRemainingColumn.setCellValueFactory(cellData -> {
            ComplaintEnt complaint = cellData.getValue();
            String timeRemaining = calculateTimeRemaining(complaint.getDate());
            return new javafx.beans.property.SimpleStringProperty(timeRemaining);
        });

        // Setup the action buttons column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button handleButton = new Button("Handle");
            private final TextField refundField = new TextField();
            private final Button confirmRefundButton = new Button("Confirm Refund");
            private final HBox buttonBox = new HBox(5, refundField, confirmRefundButton, handleButton);

            {
                refundField.setPromptText("Refund Amount");
                refundField.setMaxWidth(70);

                // Handle complaint when button is clicked
                handleButton.setOnAction(event -> {
                    try {
                        handleComplaint(getTableView().getItems().get(getIndex()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                // When clicking "Confirm Refund," store the value
                confirmRefundButton.setOnAction(event -> {
                    processRefund(getTableView().getItems().get(getIndex()), refundField.getText());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    refundField.clear(); // clear previous text when reusing cells
                    setGraphic(buttonBox);
                }
            }
        });

        // initialize complaints list
        complaints = FXCollections.observableArrayList();
        complaintsTable.setItems(complaints);
    }

    // Calculate the remaining time to handle complaint using utc time zone
    private String calculateTimeRemaining(Date complaintDate) {
        // Convert the complaintDate to UTC Date using Calendar
        Calendar complaintCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        complaintCalendar.setTime(complaintDate);
        Date complaintDateInUTC = complaintCalendar.getTime();

        // Calculate the deadline (24 hours after the complaint date)
        Calendar deadlineCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        deadlineCalendar.setTime(complaintDateInUTC);
        deadlineCalendar.add(Calendar.HOUR, 24);
        Date deadline = deadlineCalendar.getTime();

        // Get the current time in UTC
        Calendar nowCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date now = nowCalendar.getTime();

        // Check if the deadline has passed
        if (now.after(deadline)) {
            return "Time expired";
        }

        // Calculate the remaining time
        long remainingMillis = deadline.getTime() - now.getTime();
        long remainingHours = remainingMillis / (1000 * 60 * 60);
        long remainingMinutes = (remainingMillis / (1000 * 60)) % 60;

        return String.format("%02d hours, %02d minutes", remainingHours, remainingMinutes);
    }

    // Sends a request to mark the complaint as handled and removes it from the table
    private void handleComplaint(ComplaintEnt complaint) throws Exception {
        SimpleClient.getClient().sendHandleComplaint(complaint.getId(), this.refundAmount);
        //Platform.runLater(() -> complaints.remove(complaint));
    }

    // Processes the refund amount entered by the user
    private void processRefund(ComplaintEnt complaint, String amount) {
        try {
            this.refundAmount = Double.parseDouble(amount);
            System.out.println("Refunding " + this.refundAmount + " to " + complaint.getName());
        } catch (NumberFormatException e) {
            System.out.println("Invalid refund amount");// refund need to be a double (complex number)
        }
    }

    // Navigates back to the previous screen
    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }
}
