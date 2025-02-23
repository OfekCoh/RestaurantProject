package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class DishSelectionController {

    @FXML
    private GridPane dishGrid;

    @FXML
    private Button backButton;

    @FXML
    private Label menuLabel;

    @FXML
    private TextField searchField;

    private static String selectedMode;
    private static int selectedBranchId; // Stores the selected branch ID
    private static String selectedBranchName;

    private List<DishEnt> dishes;

    public static void setSelectedBranchName(String branchName) {
        selectedBranchName = branchName;
    }

    public static void setSelectedBranch(int branchId) {
        selectedBranchId = branchId;
    }

    public static void setSelectedMode(String selectedMode) {
        DishSelectionController.selectedMode = selectedMode;
    }

    public void setDishes(List<DishEnt> dishes) {
        this.dishes = dishes;
    }

    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            System.out.println(event.getDishList().toString());
            setDishes(event.getDishList());
            populateDishGrid();
        });
    }

    @FXML
    private void onSearch() {
        if (dishes == null) return;
        String searchText = searchField.getText().toLowerCase();

        // Filter dishes dynamically based on search input
        List<DishEnt> filteredDishes = dishes.stream()
                .filter(dish -> dish.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        populateDishGrid(filteredDishes);
    }

    private void populateDishGrid() {
        populateDishGrid(dishes);
    }

    private void populateDishGrid(List<DishEnt> dishList) {
        dishGrid.getChildren().clear();
        menuLabel.setText("Menu - " + selectedBranchName);

        int column = 0;
        int row = 0;

        for (DishEnt dish : dishList) {
            if (dish.getBranchID() == 0 || dish.getBranchID() == selectedBranchId) {
                VBox dishBox = createDishBox(dish);
                dishGrid.add(dishBox, column, row);

                column++;
                if (column == 2) { // 2 items per row
                    column = 0;
                    row++;
                }
            }
        }
    }

    private VBox createDishBox(DishEnt dish) {
        Image dishImg = utils.decodeBase64ToImage(dish.getImage());

        ImageView dishImage = new ImageView(dishImg);
        dishImage.setFitWidth(150);
        dishImage.setFitHeight(150);

        Label dishName = new Label(dish.getName());
        dishName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Node priceNode;
        if (dish.getIsSalePrice()) {
            Label salePriceLabel = new Label("$" + dish.getSalePrice());
            salePriceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            Label originalPriceLabel = new Label("$" + dish.getPrice());
            originalPriceLabel.setStyle("-fx-strikethrough: true; -fx-text-fill: gray;");
            priceNode = new HBox(5, salePriceLabel, originalPriceLabel);
        } else {
            priceNode = new Label("$" + dish.getPrice());
        }

        Label dishType = new Label((dish.getBranchID() == 0) ? "Chain Dish" : "Branch Dish");
        dishType.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        VBox dishBox = new VBox(10, dishImage, dishName, priceNode, dishType);
        dishBox.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-radius: 5px; -fx-background-color: #f8f8f8;");
        dishBox.setOnMouseClicked(event -> onDishSelected(dish));

        return dishBox;
    }

    private void onDishSelected(DishEnt dish) {
        System.out.println("Selected Dish ID: " + dish.getId());
        DishDetailsController.setSelectedDish(dish);
        DishDetailsController.setSelectedDishId(dish.getId());
        try {
            App.setRoot("dishDetails");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            if(selectedMode.equals("order")){
                App.setRoot("selectDeliveryLocation");
            }else{
                App.setRoot("branchSelection");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        setDishes(SimpleClient.DishList);
        System.out.println(selectedMode);
        if (dishes == null || dishes.isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        if(selectedMode.equals("order")){
            backButton.setText("Back");
        }

        menuLabel.setText("Menu - " + selectedBranchName);
        populateDishGrid();

        searchField.setFocusTraversable(false);
        dishGrid.requestFocus();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
    }
}
