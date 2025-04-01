package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.DishEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DishSelectionController {

    @FXML private GridPane dishGrid;
    @FXML private Button backButton;
    @FXML private Label menuLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    // The cart button in top-right
    @FXML private Button cartButton;

    private static String selectedMode;
    private static int selectedBranchId;
    private static String selectedBranchName;
    private List<DishEnt> dishes;

    public static void setSelectedBranchName(String branchName) {
        selectedBranchName = branchName;
    }
    public static void setSelectedBranch(int branchId) {
        selectedBranchId = branchId;
    }
    public static void setSelectedMode(String mode) {
        selectedMode = mode;
    }
    public void setDishes(List<DishEnt> dishes) {
        this.dishes = dishes;
    }

    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            setDishes(event.getDishList());
            populateDishGrid();
            onSearch();
        });
    }

    @FXML
    void initialize() {
        // If you rely on dish events, keep:
        EventBus.getDefault().register(this);

        setDishes(SimpleClient.DishList);

        // Right after load, show correct cart count
        updateCartCount();

        if (dishes == null || dishes.isEmpty()) {
            System.out.println("No dishes available!");
            return;
        }

        if ("order".equalsIgnoreCase(selectedMode)) {
            backButton.setText("Back");
        }else{
            cartButton.setVisible(false);
        }
        menuLabel.setText("Menu - " + selectedBranchName);
        populateDishGrid();


//        searchField.setFocusTraversable(false);
//        dishGrid.requestFocus();
        sortComboBox.getItems().addAll("Default", "Low to High", "High to Low");
        sortComboBox.setValue("Default");
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> onSearch());

        // Live filtering while typing
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearch());
    }

    @FXML
    private void onSearch() {
        if (dishes == null) return;
        String txt = searchField.getText().toLowerCase();
        List<DishEnt> filtered = dishes.stream()
                .filter(d -> d.getName().toLowerCase().contains(txt))
                .collect(Collectors.toList());

        if (sortComboBox != null) {
            String sortOrder = sortComboBox.getValue();
            if ("Low to High".equals(sortOrder)) {
                filtered.sort(Comparator.comparingDouble((DishEnt dish) ->
                        dish.getIsSalePrice() ? dish.getSalePrice() : dish.getPrice()));

            } else if ("High to Low".equals(sortOrder)) {
                filtered.sort(Comparator.comparingDouble((DishEnt dish) ->
                        dish.getIsSalePrice() ? dish.getSalePrice() : dish.getPrice()).reversed());
            }
        }

        populateDishGrid(filtered);
    }

    private void populateDishGrid() {
        populateDishGrid(dishes);
    }

    private void populateDishGrid(List<DishEnt> dishList) {
        dishGrid.getChildren().clear();
        int column = 0, row = 0;
        for (DishEnt dish : dishList) {
            if (dish.getBranchID() == 0 || dish.getBranchID() == selectedBranchId) {
                VBox box = createDishBox(dish);
                dishGrid.add(box, column, row);
                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }
        }
    }

    private VBox createDishBox(DishEnt dish) {
        Image dishImg = utils.decodeBase64ToImage(dish.getImage());
        ImageView dishImage = new ImageView(dishImg);
        dishImage.setPreserveRatio(true);
        dishImage.setFitWidth(200);
        // or remove setFitWidth if you want the image to scale even more

        Label name = new Label(dish.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Node priceNode;
        if (dish.getIsSalePrice()) {
            Label salePrice = new Label("$" + dish.getSalePrice());
            salePrice.setStyle("-fx-font-weight: bold;");
            Text origPrice = new Text("$" + dish.getPrice());
            origPrice.setStrikethrough(true);
            priceNode = new HBox(5, salePrice, origPrice);
        } else {
            priceNode = new Label("$" + dish.getPrice());
        }

        Label dishType = new Label((dish.getBranchID() == 0) ? "Chain Dish" : "Branch Dish");
        dishType.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        VBox box = new VBox(10, dishImage, name, priceNode, dishType);
        box.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: #f8f8f8;");

        // Let the box fill horizontally in the grid cell
        box.setMaxWidth(Double.MAX_VALUE);
        // Tells the gridpane to let this box expand
        GridPane.setHgrow(box, Priority.ALWAYS);

        box.setOnMouseClicked(e -> onDishSelected(dish));
        return box;
    }


    private void onDishSelected(DishEnt dish) {
        DishDetailsController.setSelectedDish(dish);
        DishDetailsController.setSelectedDishId(dish.getId());
        DishDetailsController.setSelectedMode(selectedMode);
        try {
            App.setRoot("dishDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            if ("order".equalsIgnoreCase(selectedMode)) {
                App.setRoot("selectDeliveryLocation");
            } else {
                App.setRoot("branchSelection");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when user clicks cart icon => navigate to cart screen.
     */
    @FXML
    private void onCartButtonClick() throws IOException {
        App.setRoot("cart");
    }

    /**
     * Refresh the cart icon text with current total items.
     */
    private void updateCartCount() {
        int count = OrderManage.getDishIds().size();
        cartButton.setText("🛒 (" + count + ")");
    }
}
