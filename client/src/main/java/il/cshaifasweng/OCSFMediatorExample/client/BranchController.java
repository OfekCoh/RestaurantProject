package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BranchController {
    public static String message = "";

    @FXML
    private Button backButton;

    @FXML
    private TableView<BranchEnt> tableView;

    @FXML
    private TableColumn<BranchEnt, Number> idColumn;

    @FXML
    private TableColumn<BranchEnt, String> nameColumn;

    @FXML
    private TableColumn<BranchEnt, String> descriptionColumn;

    @FXML
    private TableColumn<BranchEnt, String> openingHoursColumn;

    @FXML
    private TableColumn<BranchEnt, Number> tableColumn;


    private ObservableList<BranchEnt> BranchList = FXCollections.observableArrayList();

    @Subscribe
    public void onBranchEvent(BranchEvent event) {
//        System.out.println("onMenuEvent called! Updating table...");
        Platform.runLater(() -> {
            BranchList.clear();
            BranchList.addAll(event.getBranchList());
            tableView.setItems(BranchList);
            // Optionally force a refresh:
            tableView.refresh();
        });
    }

    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void initialize() {
        // Register to the eventBus
        EventBus.getDefault().register(this);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        openingHoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("tables"));



        openingHoursColumn.setCellValueFactory(cellData -> {
            String[] arr = cellData.getValue().getOpeningHours();
            // Safely handle null or empty
            String joined = (arr == null) ? "" : String.join(", ", arr);
            return new javafx.beans.property.SimpleStringProperty(joined);
        });
    }


}