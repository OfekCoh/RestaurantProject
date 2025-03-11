package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.DishEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuChangeEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuChangeEnt;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuUpdateController {

    // -------------------------------------
    // table for Dishes
    // -------------------------------------

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
    private TableColumn<DishEnt, String> toppingsColumn;
    @FXML
    private TableColumn<DishEnt, Number> priceColumn;
    @FXML
    private TableColumn<DishEnt, ComboBox<BranchEnt>> branchColumn;
    @FXML
    private TableColumn<DishEnt, Number> salePriceColumn;
    @FXML
    private TableColumn<DishEnt, Boolean> isSalePriceColumn;
    @FXML
    private TableColumn<DishEnt, Button> actionsColumn;

    private ObservableList<DishEnt> dishList = FXCollections.observableArrayList();
    private ObservableList<MenuChangeEnt> menuChangesList = FXCollections.observableArrayList();
    private List<BranchEnt> branchList = new ArrayList<>();

    // -------------------------------------
    // table for MenuChanges
    // -------------------------------------
    @FXML
    private TableView<MenuChangeEnt> menuChangeTable;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcIdColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcDishIdColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcOldPriceColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Boolean> mcOldIsOnSaleColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcOldSalePriceColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcNewPriceColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Boolean> mcNewIsOnSaleColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Number> mcNewSalePriceColumn;
    @FXML
    private TableColumn<MenuChangeEnt, Button> mcActionsColumn;

    @Subscribe
    public void onDishEvent(MenuChangeEvent event) {
        Platform.runLater(() -> {
            menuChangesList.clear();
            menuChangesList.addAll(event.getMenuChangesList());
            menuChangeTable.setItems(menuChangesList);
            menuChangeTable.refresh();
        });
    }

    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            dishList.clear();
            dishList.addAll(event.getDishList());
            tableView.setItems(dishList);
            tableView.refresh();
        });
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        try {
            SimpleClient.getClient().sendGetMenuCommand();
            SimpleClient.getClient().sendGetMenuChanges();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        menuChangeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        ingredientsColumn.setCellValueFactory(cellData -> {
            String[] arr = cellData.getValue().getIngredients();
            String joined = (arr == null) ? "" : String.join(", ", arr);
            return new javafx.beans.property.SimpleStringProperty(joined);
        });

        toppingsColumn.setCellValueFactory(cellData -> {
            String[] arr = cellData.getValue().getToppings();
            String joined = (arr == null) ? "" : String.join(", ", arr);
            return new javafx.beans.property.SimpleStringProperty(joined);
        });



        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
//        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
//        priceColumn.setOnEditCommit(event -> {
//            DishEnt dish = event.getRowValue();
//            dish.setPrice(event.getNewValue().intValue());
//            updateDishPrice(dish);
//        });

        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        isSalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("isSalePrice"));


        branchList.addAll(SimpleClient.BranchList);
        BranchEnt chainBranch = new BranchEnt();
        chainBranch.setBranchName("Chain");
        chainBranch.setId(0);
        branchList.add(0, chainBranch);

        branchColumn.setCellFactory(column -> new TableCell<DishEnt, ComboBox<BranchEnt>>() {
            @Override
            protected void updateItem(ComboBox<BranchEnt> comboBox, boolean empty) {
                super.updateItem(comboBox, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DishEnt dish = getTableView().getItems().get(getIndex());
                    ComboBox<String> branchComboBox = new ComboBox<>();

                    for (BranchEnt branch : branchList) {
                        branchComboBox.getItems().add(branch.getId() + " - " + branch.getBranchName());
                    }

                    branchComboBox.getSelectionModel().select(
                            dish.getBranchID() + " - " + branchList.stream()
                                    .filter(b -> b.getId() == dish.getBranchID())
                                    .findFirst().orElse(new BranchEnt(dish.getBranchID(), "Unknown")).getBranchName()
                    );

                    branchComboBox.setOnAction(event -> {
                        int selectedBranchId = Integer.parseInt(branchComboBox.getValue().split(" - ")[0]);
                        dish.setBranchID(selectedBranchId);
                        updateDishBranch(dish);
                    });

                    setGraphic(branchComboBox);
                }
            }
        });

        actionsColumn.setCellFactory(column -> new TableCell<DishEnt, Button>() {
            private final Button editButton = new Button("Edit Details");
            private final Button editPrices = new Button("Edit Prices");
            private final Button removeButton = new Button("Remove");
            private final HBox buttonContainer = new HBox(10, editButton, editPrices, removeButton);

            {
                editButton.setOnAction(event -> {
                    DishEnt dish = getTableView().getItems().get(getIndex());
                    openDishEditor(dish, "edit");
                });

                editPrices.setOnAction(event -> {
                    DishEnt dish = getTableView().getItems().get(getIndex());
                    openDishEditor(dish, "price");
                });

                removeButton.setOnAction(event -> {
                    DishEnt dish = getTableView().getItems().get(getIndex());
                    removeDish(dish);
                });
            }

            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });

//        tableView.setEditable(true);

        // -------------------------------------
        // Setup MenuChange table columns
        // -------------------------------------
        mcIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        mcDishIdColumn.setCellValueFactory(new PropertyValueFactory<>("dishId"));
        mcOldPriceColumn.setCellValueFactory(new PropertyValueFactory<>("oldPrice"));
        mcOldIsOnSaleColumn.setCellValueFactory(new PropertyValueFactory<>("oldIsOnSale"));
        mcOldSalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("oldSalePrice"));
        mcNewPriceColumn.setCellValueFactory(new PropertyValueFactory<>("newPrice"));
        mcNewIsOnSaleColumn.setCellValueFactory(new PropertyValueFactory<>("newIsOnSale"));
        mcNewSalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("newSalePrice"));
        mcActionsColumn.setCellFactory(column -> new TableCell<MenuChangeEnt, Button>() {
            private final Button approveButton = new Button("✔");
            private final Button rejectButton = new Button("✘");
            private final HBox buttonContainer = new HBox(5, approveButton, rejectButton);
            {
                approveButton.setDisable(SimpleClient.ruleID != 4);
                approveButton.setVisible(SimpleClient.ruleID == 4);

                approveButton.setOnAction(event -> {
                    MenuChangeEnt change = getTableView().getItems().get(getIndex());
                    handleMenuChangeDecision(change, /* approved = */ true);
                });

                rejectButton.setOnAction(event -> {
                    MenuChangeEnt change = getTableView().getItems().get(getIndex());
                    handleMenuChangeDecision(change, /* not-approved = */ false);
                });
            }



            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });
        menuChangeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void handleMenuChangeDecision(MenuChangeEnt change, boolean approved) {
        System.out.println("Decision for MenuChange #" + change.getId() + ": " + (approved ? "Approved" : "Rejected"));
        // Use the SimpleClient method: sendSetMenuUpdate
        try {
            SimpleClient.getClient().sendSetMenuUpdate(
                    change.getId(),
                    change.getDishId(),
                    change.getNewPrice(),
                    change.isNewIsOnSale(),
                    change.getNewSalePrice(),
                    approved
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Optionally remove the item from the table or refresh after the server updates it
        menuChangesList.remove(change);
        menuChangeTable.refresh();
    }


    private void updateDishBranch(DishEnt dish) {
        try {
            System.out.println("Updating branch for dish ID: " + dish.getId() + " to " + dish.getBranchID());
            SimpleClient.getClient().sendUpdateBranchCommand(dish.getId(), dish.getBranchID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDish(DishEnt dish) {
        try {
            System.out.println("Removing dish ID: " + dish.getId());
            SimpleClient.getClient().sendRemoveDishCommand(dish.getId());
            dishList.remove(dish);
            tableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDishEditor(DishEnt dish, String mode) {
        try {
            ManageDishController.setSelectedDish(dish);
            ManageDishController.setSelectedMode(mode);
            App.setRoot("manageDish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBackButton() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void handleAddDish() throws IOException {
        App.setRoot("manageDish");
    }
}
