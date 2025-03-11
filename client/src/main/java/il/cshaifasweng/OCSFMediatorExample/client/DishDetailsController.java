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
import java.util.*;

/**
 * DishDetailsController
 *
 * Lets user view or order a dish. If in "order" mode, user can:
 * - Adjust quantity
 * - Modify ingredients (None/Less/Normal/Extra)
 * - Select toppings (None/Add)
 * - Add to cart
 */
public class DishDetailsController {

    private static DishEnt selectedDish;
    private static int selectedDishId;
    private static String selectedMode;

    @FXML private Label dishNameLabel;
    @FXML private Label dishDescriptionLabel;
    @FXML private Label dishIngredientsLabel;
    @FXML private Label dishToppingsShownLabel;
    @FXML private Label dishPriceLabel;
    @FXML private Label dishTypeLabel;
    @FXML private ImageView dishImageView;

    // Quantity row
    @FXML private Label quantityText;  // label "Quantity:"
    @FXML private Label quantityLabel; // numeric label
    @FXML private Button quantityMinusButton;
    @FXML private Button quantityPlusButton;

    // Ingredient adaptation (None/Less/Normal/Extra) , only if mode = "order"
    @FXML private Label ingredientsAdaptationLabel;
    @FXML private VBox ingredientAdaptationBox;

    @FXML private VBox toppingsBox;
    @FXML private Label toppingsLabel;

    // "Add to Cart" button
    @FXML private Button addToCartButton;

    // Cart button
    @FXML private Button cartButton;

    // "Back" button
    @FXML private Button backButton;

    // Map of ingredient => (None/Less/Normal/Extra)
    private Map<String, String> adaptationMap = new LinkedHashMap<>();

    // Map of topping => (None/Add)
    private Map<String, String> toppingMap = new LinkedHashMap<>();

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

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        if (selectedDish == null) {
            System.out.println("No dish found for details!");
            return;
        }

        displayDishInfo();
        quantity = 1;
        updateQuantityLabel();

        // Show/hide the special "order" controls
        boolean isOrderMode = "order".equalsIgnoreCase(selectedMode);
        showOrderControls(isOrderMode);
        if (isOrderMode) {
            buildAdaptationUI(selectedDish); // For ingredients
            buildToppingsUI(selectedDish);    // For toppings
        }

        updateCartCount();
    }

    /**
     * If we still rely on dish events from server to refresh, keep this method.
     * Otherwise, it can be removed.
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

    private void showOrderControls(boolean isOrder) {
        quantityText.setVisible(isOrder);
        quantityLabel.setVisible(isOrder);
        quantityMinusButton.setVisible(isOrder);
        quantityPlusButton.setVisible(isOrder);
        ingredientAdaptationBox.setVisible(isOrder);
        addToCartButton.setVisible(isOrder);
        cartButton.setVisible(isOrder);
        toppingsBox.setVisible(isOrder);
        toppingsLabel.setVisible(isOrder);
        ingredientsAdaptationLabel.setVisible(isOrder);
    }

    /**
     * Fill in name, description, ingredients, price, type, image, etc.
     */
    private void displayDishInfo() {
        dishNameLabel.setText(selectedDish.getName());
        dishDescriptionLabel.setText(selectedDish.getDescription());

        StringJoiner joiner = new StringJoiner(", ");
        for (String ing : selectedDish.getIngredients()) {
            joiner.add(ing);
        }
        dishIngredientsLabel.setText("Ingredients: " + joiner);

        if (selectedDish.getToppings() == null || selectedDish.getToppings().length == 0) {
            dishToppingsShownLabel.setManaged(false);
            dishToppingsShownLabel.setVisible(false);
        } else {
            dishToppingsShownLabel.setManaged(true);
            dishToppingsShownLabel.setVisible(true);
            StringJoiner topJoiner = new StringJoiner(", ");
            for (String topping : selectedDish.getToppings()) {
                topJoiner.add(topping);
            }
            dishToppingsShownLabel.setText("Available Toppings: " + topJoiner);
        }

        if (selectedDish.getIsSalePrice()) {
            dishPriceLabel.setText("Sale Price: $" + selectedDish.getSalePrice()
                    + " (Original: $" + selectedDish.getPrice() + ")");
        } else {
            dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
        }

        dishTypeLabel.setText(selectedDish.getBranchID() == 0 ? "Chain Dish" : "Branch Dish");
        dishImageView.setImage(utils.decodeBase64ToImage(selectedDish.getImage()));
    }

    /**
     * Build the UI for ingredient adaptation.
     * Each ingredient gets a ChoiceBox with None/Less/Normal/Extra.
     */
    private void buildAdaptationUI(DishEnt dish) {
        ingredientAdaptationBox.getChildren().clear();
        adaptationMap.clear();

        if (dish.getIngredients() == null || dish.getIngredients().length == 0) {
            // If somehow no ingredients, hide the entire box
            ingredientAdaptationBox.setVisible(false);
            return;
        }
        ingredientAdaptationBox.setVisible(true);

        for (String ingredient : dish.getIngredients()) {
            adaptationMap.put(ingredient, "Normal");
            Label ingredientLabel = new Label(ingredient + ":");
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

    /**
     * Build the UI for toppings: each topping => (None, Add).
     */
    private void buildToppingsUI(DishEnt dish) {
        toppingsBox.getChildren().clear();
        toppingMap.clear();

        if (dish.getToppings() == null || dish.getToppings().length == 0) {
            toppingsBox.setVisible(false);
            toppingsLabel.setVisible(false);
            return;
        }
        toppingsBox.setVisible(true);
        toppingsLabel.setVisible(true);

        for (String topping : dish.getToppings()) {
            toppingMap.put(topping, "None");

            Label toppingLabel = new Label(topping + ":");
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().addAll("None", "Add");
            choiceBox.setValue("None");

            choiceBox.setOnAction(e -> {
                toppingMap.put(topping, choiceBox.getValue());
            });

            HBox row = new HBox(10, toppingLabel, choiceBox);
            toppingsBox.getChildren().add(row);
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

        updateCartCount(); // Refresh cart icon
        System.out.println("Added dish [" + selectedDish.getId()
                + "] x" + quantity
                + " with adaptation => " + adaptationString);
    }

    /**
     * Combine ingredient adaptation (excluding Normal) and topping selection (only Add).
     */
    private String buildAdaptationString() {
        StringBuilder sb = new StringBuilder();

        // 1) Ingredient adaptation (None/Less/Normal/Extra)
        for (Map.Entry<String, String> entry : adaptationMap.entrySet()) {
            // Only show it if NOT Normal
            if (!"Normal".equalsIgnoreCase(entry.getValue())) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("; ");
            }
        }

        // 2) Toppings (None/Add)
        for (Map.Entry<String, String> entry : toppingMap.entrySet()) {
            // Only show it if Add
            if ("Add".equalsIgnoreCase(entry.getValue())) {
                sb.append(entry.getKey())
                        .append(": Add; ");
            }
        }

        return sb.toString().trim();
    }

    // ---------------- NAVIGATION ----------------
    @FXML
    private void onBackClick() throws IOException {
        App.setRoot("dishSelection");
    }

    @FXML
    private void onCartButtonClick() throws IOException {
        App.setRoot("cart");
    }

    /**
     * Rebuild the cart icon text with the current item count from some global cart manager.
     */
    private void updateCartCount() {
        int count = OrderManage.getDishIds().size(); // or however your cart is tracked
        cartButton.setText("🛒 (" + count + ")");
    }
}
