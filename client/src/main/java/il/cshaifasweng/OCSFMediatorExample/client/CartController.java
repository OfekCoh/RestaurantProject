package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.DishEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    private double totalPrice;


    @FXML
    private ListView<Integer> cartListView; // Store item *indices*, not strings
    @FXML
    private Label deliveryCostLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button clearCartButton;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        // Setup the custom cell factory
        cartListView.setCellFactory(listView -> new CartItemCell(this));

        refreshCartDisplay();
    }

    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            // Optionally update the global dish list if needed:
            SimpleClient.DishList = event.getDishList();

            // Refresh the cart display so that updated dish details are shown
            refreshCartDisplay();
        });
    }

    /**
     * Rebuild the items in the list to match the current cart contents
     * (0..N-1 for each item).
     */
    public void refreshCartDisplay() {
        // Convert the range of indices [0..cartSize-1] into a list
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < OrderManage.getDishIds().size(); i++) {
            indices.add(i);
        }

        cartListView.setItems(FXCollections.observableList(indices));

        // Now recalc total price
        totalPrice = 0;
        if (OrderManage.getOrderType().equalsIgnoreCase("Delivery")) {
            totalPrice += OrderManage.getDeliveryFixedCost();
        }
        List<Integer> dishIds = OrderManage.getDishIds();
        for (int i = 0; i < dishIds.size(); i++) {
            DishEnt dish = findDishById(dishIds.get(i));
            if (dish == null) continue;
            double price = dish.getIsSalePrice() ? dish.getSalePrice() : dish.getPrice();
            totalPrice += price;
        }
        //In case we add the delivery Fixed cost (some java handling can be odd)
        DecimalFormat df = new DecimalFormat("#.##");
        totalPrice = Double.parseDouble(df.format(totalPrice));


        //Check if it is an delivery order, or regular.

        if (OrderManage.getOrderType().equalsIgnoreCase("Delivery")) {
            deliveryCostLabel.setVisible(true);
            deliveryCostLabel.setText("Delivery Cost: $" + OrderManage.getDeliveryFixedCost());
            totalPriceLabel.setText(String.format("Total (Including Delivery): $%.2f", totalPrice));
        } else {
            deliveryCostLabel.setVisible(false);
            totalPriceLabel.setText(String.format("Total: $%.2f", totalPrice));
        }

    }

    private DishEnt findDishById(int dishId) {
        if (SimpleClient.DishList == null) return null;
        for (DishEnt dish : SimpleClient.DishList) {
            if (dish.getId() == dishId) {
                return dish;
            }
        }
        return null;
    }

    @FXML
    private void onClearCart() {
        OrderManage.clearCart();
        refreshCartDisplay();
    }

    @FXML
    private void onCheckout() {
        try {
            System.out.println("Proceeding to checkout with " + OrderManage.getDishIds().size() + " items.");
            if (!OrderManage.getDishIds().isEmpty()) {
                OrderManage.setFinalPrice(totalPrice);
                System.out.println("FinalPrice: " + OrderManage.getFinalPrice());
                BuyerDetailsFormController.setPreviousFXML("order");
                App.setRoot("buyerForm");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        String.format("Message: %s\n",
                                "Your cart is empty! can't proceed to checkout!"
                        ));
                alert.show();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ...
    }

    @FXML
    private void onBack() throws IOException {
        App.setRoot("dishSelection");
    }

    /**
     * Custom cell that displays name + price + adaptation, plus (+) and minus (-) buttons.
     */
    private static class CartItemCell extends ListCell<Integer> {
        private final CartController parentController;

        private HBox hbox;
        private Label infoLabel; // Dish name/price/adaptation
        private Button plusBtn;
        private Button minusBtn;

        public CartItemCell(CartController parent) {
            this.parentController = parent;
            hbox = new HBox(10);

            infoLabel = new Label();
            plusBtn = new Button("+");
            minusBtn = new Button("-");

            // Set up button actions
            plusBtn.setOnAction(e -> handlePlus());
            minusBtn.setOnAction(e -> handleMinus());

            hbox.getChildren().addAll(infoLabel, plusBtn, minusBtn);
        }

        /**
         * Called whenever this cell is updated to show a new item index
         * or becomes empty.
         */
        @Override
        protected void updateItem(Integer index, boolean empty) {
            super.updateItem(index, empty);

            if (empty || index == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Build text from the dish data
                // index is which item in CartManage
                List<Integer> dishIds = OrderManage.getDishIds();
                List<String> adaptations = OrderManage.getAdaptations();
                if (index < 0 || index >= dishIds.size()) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                int dishId = dishIds.get(index);
                String adapt = adaptations.get(index);

                DishEnt dish = parentController.findDishById(dishId);
                if (dish == null) {
                    setText("(Unknown Dish)");
                    setGraphic(null);
                    return;
                }

                double price = dish.getIsSalePrice() ? dish.getSalePrice() : dish.getPrice();
                StringBuilder sb = new StringBuilder();
                sb.append(dish.getName())
                        .append(" ($").append(price).append(")");
                if (adapt != null && !adapt.isBlank()) {
                    sb.append(" [").append(adapt).append("]");
                }

                infoLabel.setText(sb.toString());
                setText(null);
                setGraphic(hbox);
            }
        }

        /**
         * Duplicate the current item: add one more copy with the same dishId & adaptation.
         */
        private void handlePlus() {
            Integer index = getItem();
            if (index == null) return;
            if (index >= 0 && index < OrderManage.getDishIds().size()) {
                int dishId = OrderManage.getDishIds().get(index);
                String adapt = OrderManage.getAdaptations().get(index);
                // Add 1 more copy
                OrderManage.addDishes(dishId, 1, adapt);

                // Refresh the cart list
                parentController.refreshCartDisplay();
            }
        }

        /**
         * Remove the current item from the cart
         */
        private void handleMinus() {
            Integer index = getItem();
            if (index == null) return;
            if (index >= 0 && index < OrderManage.getDishIds().size()) {
                // Remove that item
                OrderManage.removeItem(index);

                // Refresh
                parentController.refreshCartDisplay();
            }
        }
    }
}
