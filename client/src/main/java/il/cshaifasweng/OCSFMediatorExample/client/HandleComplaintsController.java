///**
// * Sample Skeleton for 'handleComplaints.fxml' Controller Class
// */
//
//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.ComplaintEnt;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//
//import java.util.List;
//
//public class HandleComplaintsController {
//
//    @FXML // fx:id="complaintHandledColumn"
//    private TableColumn<?, ?> finishButtonColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="complaintTextColumn"
//    private TableColumn<?, ?> TextColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="compliantIDColumn"
//    private TableColumn<?, ?> IDColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="compliantIDColumn"
//    private TableColumn<?, ?> DateColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="nameOfBuyerColumn"
//    private TableColumn<?, ?> nameOfBuyerColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="refundButtonColumn"
//    private TableColumn<?, ?> refundButtonColumn; // Value injected by FXMLLoader
//
//    @FXML // fx:id="tableView"
//    private TableView<?> tableView; // Value injected by FXMLLoader
//
//    private ObservableList<ComplaintEnt> complaints;
//
//    public void initializeData(List<ComplaintEnt> complaints) {
//        complaints = FXCollections.observableArrayList(complaints);
//
//        // Set up columns
//        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        TextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
//        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
//
//
//    }
package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuChangeEvent;
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
import java.util.List;

public class HandleComplaintsController {

    private int refundAmount;

    @FXML
    private Button backButton;

    @FXML
    private TableView<ComplaintEnt> complaintsTable;
    @FXML
    private TableColumn<ComplaintEnt, Integer> idColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> nameColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> dateColumn;
    @FXML
    private TableColumn<ComplaintEnt, String> complaintColumn;
    @FXML
    private TableColumn<ComplaintEnt, Void> actionColumn;

    private ObservableList<ComplaintEnt> complaints;

    //recieve complaints from server using event bus
    @Subscribe
    public void onCompliantEvent(ComplaintEvent event) {
        Platform.runLater(() -> {
            System.out.println("ComplaintEvent");
            // Filter complaints to only include those with status == 0
            List<ComplaintEnt> filteredComplaints = event.getComplaints().stream()
                    .filter(c -> c.getStatus() == 0)
                    .toList();
            complaints.clear();
            complaints.addAll(filteredComplaints);
            complaintsTable.setItems(complaints);
            complaintsTable.refresh();
        });
    }

    public void initialize() {
        // recieve the compliants table from the server (through client and event bus)
        EventBus.getDefault().register(this);
        try {
            SimpleClient.getClient().sendGetComplaints();
            System.out.println("sent get complaints");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // map table columns to ComplaintEnt object properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        complaintColumn.setCellValueFactory(new PropertyValueFactory<>("complaintText"));

        // buttons columns
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button handleButton = new Button("Handle");
            private final TextField refundField = new TextField();
            private final Button confirmRefundButton = new Button("Confirm Refund");
            private final HBox buttonBox = new HBox(5, refundField, confirmRefundButton, handleButton);

            {
                refundField.setPromptText("Refund Amount");
                refundField.setMaxWidth(70);

                // handle complaint
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
                    setGraphic(buttonBox);
                }
            }
        });

        complaints = FXCollections.observableArrayList();
        complaintsTable.setItems(complaints);
    }


    private void handleComplaint(ComplaintEnt complaint) throws Exception {
        SimpleClient.getClient().sendHandleComplaint(complaint.getId(), this.refundAmount);
    }

    private void processRefund(ComplaintEnt complaint, String amount) {
        try {
            this.refundAmount = Integer.parseInt(amount);
            System.out.println("Refunding " + this.refundAmount + " to " + complaint.getName());


        } catch (NumberFormatException e) {
            System.out.println("Invalid refund amount");
        }
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

}