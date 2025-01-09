package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

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

public class MenuController {
    public static String message = "";

    @FXML
    private Button backButton;

    @FXML
    private TableView<DishEnt> tableView;

    @FXML
    private TableColumn<DishEnt, Number> idColumn;

    @FXML
    private TableColumn<DishEnt, String> nameColumn;

    @FXML
    private TableColumn<DishEnt, String> descriptionColumn;

    @FXML
    private TableColumn<DishEnt, String> ingredientsColumn;

    @FXML
    private TableColumn<DishEnt, Number> priceColumn;

    private ObservableList<DishEnt> dishList = FXCollections.observableArrayList();

    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        dishList.clear();
        dishList.addAll(event.getMenu().getListDishes());
        tableView.setItems(dishList);
    }

    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void initialize() {
        // Register to the eventBus
        EventBus.getDefault().register(this);

        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Enable editing for the price column
        tableView.setEditable(true);
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            DishEnt dish = event.getRowValue();
            dish.setPrice(event.getNewValue().intValue());
            try {
                handlePriceUpdate(dish);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handlePriceUpdate(DishEnt dish) throws IOException {
//        System.out.println("Price updated for dish ID: " + dish.getId() + ", New Price: " + dish.getPrice());
        //Send an update price request to the server
        SimpleClient.getClient().sendToServer("UpdatePrice: "+dish.getId()+ " Price: " + dish.getPrice());

        // Implement logic to update the dish price in the backend/database if needed
    }
}