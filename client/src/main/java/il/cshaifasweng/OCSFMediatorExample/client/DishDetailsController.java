package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.DishEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * DishDetailsController
 *
 * Displays dish info (name, description, ingredients, price, image),
 * plus "quantity +/-" controls and ingredient adaptation (none/less/normal/extra)
 * if in "order" mode.
 *
 * Also features a cart icon button on the top-right showing the cart count.
 */
public class DishDetailsController {

    private static DishEnt selectedDish;
    private static int selectedDishId;
    private static String selectedMode;

    @FXML private Label dishNameLabel;
    @FXML private Label dishDescriptionLabel;
    @FXML private Label dishIngredientsLabel;
    @FXML private Label dishPriceLabel;
    @FXML private Label dishTypeLabel;
    @FXML private ImageView dishImageView;

    // Quantity row
    @FXML private Label quantityText;  // label "Quantity:"
    @FXML private Label quantityLabel; // numeric label
    @FXML private Button quantityMinusButton;
    @FXML private Button quantityPlusButton;

    // Adaptation box, only if mode = "order"
    @FXML private VBox ingredientAdaptationBox;
    @FXML private Button addToCartButton;

    // Cart button on the top-right
    @FXML private Button cartButton;

    // For the ingredient states: "None", "Less", "Normal", "Extra"
    private Map<String, String> adaptationMap = new LinkedHashMap<>();
    private int quantity = 1;
    private static final List<String> ADAPTATION_CHOICES = List.of("None", "Less", "Normal", "Extra");

    public static void setSelectedDish(DishEnt dish) {
        selectedDish = dish;
    }
    public static void setSelectedDishId(int id) {
        selectedDishId = id;
    }
    public static void setSelectedMode(String mode) {
        selectedMode = mode;
    }

    /**
     * (Optional) If you still rely on DishEvent for updates,
     * keep or remove as needed. We'll leave it in, but it isn't strictly required.
     */
    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            setSelectedDish(event.getDishList().stream()
                    .filter(d -> d.getId() == selectedDishId)
                    .findFirst()
                    .orElse(null));
            displayDishInfo();
        });
    }

    @FXML
    void initialize() {
        // If you still handle dish events, keep this:
        EventBus.getDefault().register(this);

        if (selectedDish == null) {
            System.out.println("No dish found for details!");
            return;
        }

        displayDishInfo();
        quantity = 1;
        updateQuantityLabel();

        if ("order".equalsIgnoreCase(selectedMode)) {
            showOrderControls(true);
            buildAdaptationUI(selectedDish);
        } else {
            showOrderControls(false);
        }

        // IMPORTANT: Refresh the cart icon count
        updateCartCount();
    }

    /**
     * Show/hide ordering controls if we are in "order" mode.
     */
    private void showOrderControls(boolean isOrder) {
        quantityText.setVisible(isOrder);
        quantityLabel.setVisible(isOrder);
        quantityMinusButton.setVisible(isOrder);
        quantityPlusButton.setVisible(isOrder);
        ingredientAdaptationBox.setVisible(isOrder);
        addToCartButton.setVisible(isOrder);
    }

    /**
     * Display dish name, description, ingredients, price, type, image.
     */
    private void displayDishInfo() {
        dishNameLabel.setText(selectedDish.getName());
        dishDescriptionLabel.setText(selectedDish.getDescription());

        StringJoiner joiner = new StringJoiner(", ");
        for (String ing : selectedDish.getIngredients()) {
            joiner.add(ing);
        }
        dishIngredientsLabel.setText("Ingredients: " + joiner);

        if (selectedDish.getIsSalePrice()) {
            dishPriceLabel.setText("Sale Price: $"
                    + selectedDish.getSalePrice()
                    + "   Original: $"
                    + selectedDish.getPrice());
        } else {
            dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
        }

        dishTypeLabel.setText(selectedDish.getBranchID() == 0 ? "Chain Dish" : "Branch Dish");
        dishImageView.setImage(utils.decodeBase64ToImage(selectedDish.getImage()));
    }

    /**
     * Build the adaptation UI for each ingredient: a ChoiceBox with None/Less/Normal/Extra
     */
    private void buildAdaptationUI(DishEnt dish) {
        ingredientAdaptationBox.getChildren().clear();
        adaptationMap.clear();

        for (String ingredient : dish.getIngredients()) {
            adaptationMap.put(ingredient, "Normal");
            Label ingredientLabel = new Label(ingredient);
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().addAll(ADAPTATION_CHOICES);
            choiceBox.setValue("Normal");
            choiceBox.setOnAction(e -> {
                adaptationMap.put(ingredient, choiceBox.getValue());
            });

            HBox row = new HBox(10, ingredientLabel, choiceBox);
            ingredientAdaptationBox.getChildren().add(row);
        }
    }

    // ---------------- QUANTITY CONTROLS ----------------
    @FXML
    private void onQuantityMinus() {
        if (quantity > 1) {
            quantity--;
            updateQuantityLabel();
        }
    }

    @FXML
    private void onQuantityPlus() {
        quantity++;
        updateQuantityLabel();
    }

    private void updateQuantityLabel() {
        quantityLabel.setText(String.valueOf(quantity));
    }

    // ---------------- ADD TO CART ----------------
    @FXML
    private void onAddToCart() {
        String adaptationString = buildAdaptationString();
        // Add 'quantity' items to cart
        OrderManage.addDishes(selectedDish.getId(), quantity, adaptationString);

        // Update *this* scene's cart count right away
        updateCartCount();

        System.out.println("Added dish [" + selectedDish.getId()
                + "] x" + quantity
                + " with adaptation => " + adaptationString);
    }

    private String buildAdaptationString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : adaptationMap.entrySet()) {
            if (!"Normal".equalsIgnoreCase(entry.getValue())) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("; ");
            }
        }
        return sb.toString().trim();
    }

    @FXML
    private void onBackClick() throws IOException {
        App.setRoot("dishSelection");
    }

    /**
     * User clicked the cart button => go to cart screen
     */
    @FXML
    private void onCartButtonClick() throws IOException {
        App.setRoot("cart");
    }

    /**
     * Rebuild the cart icon text with the current item count from CartManage.
     */
    private void updateCartCount() {
        int count = OrderManage.getDishIds().size();
        cartButton.setText("🛒 (" + count + ")");
    }
}
