package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.StringJoiner;

public class DishDetailsController {

    @FXML
    private Label dishNameLabel;

    @FXML
    private Label dishDescriptionLabel;

    @FXML
    private Label dishIngredientsLabel;

    @FXML
    private Label dishPriceLabel;

    @FXML
    private Label dishTypeLabel;

    @FXML
    private ImageView dishImageView;

    @FXML
    private Button backButton;

    private static DishEnt selectedDish;
    private static int selectedDishId;

    public static void setSelectedDish(DishEnt dish) {
        selectedDish = dish;
    }

    public static void setSelectedDishId(int id) {
        selectedDishId = id;
    }

    @Subscribe
    public void onDishEvent(DishEvent event) {
        Platform.runLater(() -> {
            setSelectedDish(event.getDishList()
                    .stream()
                    .filter(dish -> dish.getId() == selectedDishId)
                    .findFirst()
                    .orElse(null));
            displayDish();
        });
    }

    public void displayDish() {
        dishNameLabel.setText(selectedDish.getName());
        dishDescriptionLabel.setText(selectedDish.getDescription());

        StringJoiner ingredientsJoiner = new StringJoiner(", ");
        for (String ingredient : selectedDish.getIngredients()) {
            ingredientsJoiner.add(ingredient);
        }
        dishIngredientsLabel.setText("Ingredients: " + ingredientsJoiner.toString());

        if(selectedDish.getIsSalePrice()){
            dishPriceLabel.setText("Sale Price: $" + selectedDish.getSalePrice() + "   Original: $" + selectedDish.getPrice());
        } else {
            dishPriceLabel.setText("Price: $" + selectedDish.getPrice());
        }

        dishTypeLabel.setText((selectedDish.getBranchID() == 0) ? "Chain Dish" : "Branch Dish");

        dishImageView.setImage(utils.decodeBase64ToImage(selectedDish.getImage()));
    }



    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        if (selectedDish == null) {
            return;
        }
        displayDish();
    }

    @FXML
    private void onBackClick() throws IOException {
        App.setRoot("dishSelection");
    }
}
