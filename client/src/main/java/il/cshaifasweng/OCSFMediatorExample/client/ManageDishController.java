package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.App;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class ManageDishController {

    @FXML
    private Label titleDish;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> branchComboBox;
    @FXML
    private TextField ingredientField;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button removeIngridient;
    @FXML
    private ListView<String> ingredientListView;
    @FXML
    private TextField toppingField;
    @FXML
    private Button addToppingButton;
    @FXML
    private Button removeToppingButton;
    @FXML
    private ListView<String> toppingListView;

    @FXML
    private TextField imageField;        // Holds the base64 string (optional)
    @FXML
    private Button chooseImageButton;    // Button to pick an image
    @FXML
    private TextField salePriceField;
    @FXML
    private CheckBox isSalePriceCheckBox;
    @FXML
    private ImageView imagePreview;      // The ImageView to show the actual image

    private static DishEnt editingDish = null;
    private static String selectedMode;
    private List<BranchEnt> branchList = new ArrayList<>();
    private List<String> ingredientList = new ArrayList<>();
    private List<String> toppingsList = new ArrayList<>();

    public static void setSelectedDish(DishEnt dish) {
        editingDish = dish;
    }

    public static void setSelectedMode(String mode) {
        selectedMode = mode;
    }

    @FXML
    public void initialize() {
        // Populate branches
        branchList.add(new BranchEnt(0, "Chain")); // chain branch
        branchList.addAll(SimpleClient.BranchList);

        // Show them in the combo box as "id - name"
        List<String> branchDisplayList = new ArrayList<>();
        for (BranchEnt branch : branchList) {
            branchDisplayList.add(branch.getId() + " - " + branch.getBranchName());
        }
        branchComboBox.setItems(FXCollections.observableArrayList(branchDisplayList));

        // If we are editing an existing dish
        if (editingDish != null) {
            imagePreview.setImage(utils.decodeBase64ToImage(editingDish.getImage()));


            if (Objects.equals(selectedMode, "edit")) {
                titleDish.setText("Edit Dish");
                priceField.setDisable(true);
                salePriceField.setDisable(true);
                isSalePriceCheckBox.setDisable(true);
            } else if (Objects.equals(selectedMode, "price")) {
                titleDish.setText("Edit Price");
                nameField.setDisable(true);
                descriptionField.setDisable(true);
                branchComboBox.setDisable(true);
                //Ingredients
                ingredientField.setDisable(true);
                addIngredientButton.setDisable(true);
                removeIngridient.setDisable(true);
                //Toppings
                toppingField.setDisable(true);
                addToppingButton.setDisable(true);
                removeToppingButton.setDisable(true);

                imageField.setDisable(true);
                chooseImageButton.setDisable(true);
            }
            loadDishData(editingDish);
        } else {
            titleDish.setText("Add Dish");
        }
    }

    private void loadDishData(DishEnt dish) {
        nameField.setText(dish.getName());
        descriptionField.setText(dish.getDescription());
        priceField.setText(String.valueOf(dish.getPrice()));
        salePriceField.setText(String.valueOf(dish.getSalePrice()));
        isSalePriceCheckBox.setSelected(dish.getIsSalePrice());

        // Load existing ingredients
        ingredientListView.getItems().setAll(dish.getIngredients());
        for (String ing : dish.getIngredients()) {
            ingredientList.add(ing);
        }

        // Load existing ingredients
        toppingListView.getItems().setAll(dish.getToppings());
        for (String topping : dish.getToppings()) {
            toppingsList.add(topping);
        }

        // Load image (Base64). Display it in the text field (optional).
        imageField.setText(dish.getImage());

        // Decode Base64 and display in ImageView
        if (dish.getImage() != null && !dish.getImage().isBlank()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(dish.getImage());
                Image fxImage = new Image(new ByteArrayInputStream(decodedBytes));
                imagePreview.setImage(fxImage);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to decode base64 image: " + e.getMessage());
            }
        }

        // Select the correct branch in the ComboBox
        for (BranchEnt branch : branchList) {
            if (branch.getId() == dish.getBranchID()) {
                branchComboBox.getSelectionModel().select(branch.getId() + " - " + branch.getBranchName());
                break;
            }
        }
    }

    @FXML
    private void addTopping() {
        String topping = toppingField.getText().trim();
        if (!topping.isEmpty()) {
            toppingsList.add(topping);
            toppingListView.getItems().add(topping);
            toppingField.clear();
        }
    }

    @FXML
    private void removeSelectedTopping() {
        String selected = toppingListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            toppingsList.remove(selected);
            toppingListView.getItems().remove(selected);
        }
    }


    @FXML
    private void chooseImage() {
        // Let the user pick a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Read bytes from file
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                // Convert to Base64
                String base64Image = Base64.getEncoder().encodeToString(fileBytes);

                // Put the Base64 string into the text field
                imageField.setText(base64Image);

                // Also display in the ImageView
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                Image fxImage = new Image(new ByteArrayInputStream(decodedBytes));
                imagePreview.setImage(fxImage);

//                System.out.println("Image successfully converted to Base64.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addIngredient() {
        String ingredient = ingredientField.getText().trim();
        if (!ingredient.isEmpty()) {
            ingredientList.add(ingredient);
            ingredientListView.getItems().add(ingredient);
            ingredientField.clear();
        }
    }

    @FXML
    private void removeSelectedIngredient() {
        String selectedIngredient = ingredientListView.getSelectionModel().getSelectedItem();
        if (selectedIngredient != null) {
            ingredientList.remove(selectedIngredient);
            ingredientListView.getItems().remove(selectedIngredient);
        }
    }

    @FXML
    private void submitDish() throws Exception {
        // --- VALIDATION ---
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required.");
            return;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showError("Description is required.");
            return;
        }
        if (branchComboBox.getValue() == null) {
            showError("Please select a branch.");
            return;
        }
        if (priceField.getText().trim().isEmpty()) {
            showError("Price is required.");
            return;
        }
        double parsedPrice;
        try {
            parsedPrice = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Price must be a valid number.");
            return;
        }
        // Ensure price >= 0
        if (parsedPrice < 0) {
            showError("Price cannot be negative.");
            return;
        }

        if (ingredientList.isEmpty()) {
            showError("At least one ingredient is required.");
            return;
        }

        // Sale price (optional, but if not empty, must be valid and >= 0)
        double parsedSalePrice = 0.0;
        if (!salePriceField.getText().trim().isEmpty()) {
            try {
                parsedSalePrice = Double.parseDouble(salePriceField.getText().trim());
            } catch (NumberFormatException e) {
                showError("Sale Price must be a valid number (or blank).");
                return;
            }
            if (parsedSalePrice < 0) {
                showError("Sale Price cannot be negative.");
                return;
            }
        }

        // All required fields are filled & valid, proceed:
        int branchId = Integer.parseInt(branchComboBox.getValue().split(" - ")[0]);

        // 2) Build a new DishEnt object
        DishEnt updatedDish = new DishEnt(
                nameField.getText(),
                descriptionField.getText(),
                ingredientList.toArray(new String[0]),
                toppingsList.toArray(new String[0]),
                parsedPrice,
                branchId,
                imageField.getText(),
                isSalePriceCheckBox.isSelected(),
                parsedSalePrice
        );

        // 3) If editing an existing dish, set the same ID
        if (editingDish != null) {
            if (Objects.equals(selectedMode, "edit")) { //Edit
                updatedDish.setId(editingDish.getId());
                System.out.println("Updating existing dish: " + updatedDish);
                SimpleClient.getClient().sendUpdateDishCommand(updatedDish);
            } else if(Objects.equals(selectedMode, "price")) { //Price update
                if(SimpleClient.ruleID==4){ //CEO - has permission to edit
                    System.out.println("Updating prices for dish: " + updatedDish);
                    SimpleClient.getClient().sendUpdatePriceCommand(editingDish.getId(), updatedDish.getPrice(), updatedDish.getIsSalePrice(), updatedDish.getSalePrice());
                }else{ //Not CEO - will need to be approved by the ceo.
                    System.out.println("adding menuChanges for dish"+ editingDish +" After: \n"+updatedDish);
                    SimpleClient.getClient().sendAddMenuChange(editingDish.getId(), editingDish.getPrice(), editingDish.getIsSalePrice(), editingDish.getSalePrice(), updatedDish.getPrice(), updatedDish.getIsSalePrice(), updatedDish.getSalePrice());
                }
            }

        } else {
            System.out.println("Adding new dish: " + updatedDish);
            SimpleClient.getClient().sendAddDishCommand(updatedDish);
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            editingDish = null;
            App.setRoot("menuUpdate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
