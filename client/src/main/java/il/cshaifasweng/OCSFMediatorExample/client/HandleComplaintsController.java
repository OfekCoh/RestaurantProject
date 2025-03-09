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
//    private ObservableList<ComplaintEnt> complaintList;
//
//    public void initializeData(List<ComplaintEnt> complaints) {
//        complaintList = FXCollections.observableArrayList(complaints);
//
//        // Set up columns
//        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        TextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
//        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
//
//
//    }
