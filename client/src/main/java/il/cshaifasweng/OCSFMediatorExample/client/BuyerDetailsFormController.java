package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.IntStream;

public class BuyerDetailsFormController {

    // who called this page
    private static String callerType;

    // Person Info
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailText;

    // Payment Info
    @FXML
    private TextField idField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private ComboBox<Integer> monthCombo;
    @FXML
    private ComboBox<Integer> yearCombo;
    @FXML
    private TextField cvvField;

    // Labels
    @FXML
    private Label header;
    @FXML
    private Label adressLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label totalCostLabel;

    // Buttons
    @FXML
    private Button submitButton;
    @FXML
    private Button backButton;

    // Methods
    @FXML
    void initialize() {
        // 1) Fill month/year combos from current date => next 10 years
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // For month, we can list 1..12 (or only currentMonth..12 if you want a tighter logic).
        // We'll do 1..12 for demonstration, then validate in onSubmit.
        monthCombo.getItems().addAll(IntStream.rangeClosed(1, 12).boxed().toList());

        // For year, from currentYear..(currentYear+10)
        yearCombo.getItems().addAll(IntStream.rangeClosed(currentYear, currentYear + 10).boxed().toList());

        // Optionally set default selection to currentMonth / currentYear
        monthCombo.setValue(currentMonth);
        yearCombo.setValue(currentYear);

        // Will be available only for complaint
        emailLabel.setVisible(false);
        emailText.setVisible(false);

        updateUIBasedOnCallerType(); // change the lables as you wish
    }

    @FXML
    private void onSubmit() throws IOException {
        // Gather personal info
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String email = emailText.getText().trim();
        // Payment info
        String userID = idField.getText().trim();
        String cardNumber = cardNumberField.getText().trim();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        String cvv = cvvField.getText().trim();

        boolean checkFields;
        if(callerType.equals("complaint")) {
            checkFields = name.isEmpty() || phone.isEmpty() || email.isEmpty() || userID.isEmpty() || cardNumber.isEmpty() || cvv.isEmpty() || month == null || year == null;
        }
        else{
            checkFields = name.isEmpty() || phone.isEmpty() || address.isEmpty() || userID.isEmpty() || cardNumber.isEmpty() || cvv.isEmpty() || month == null || year == null;
        }
        // Basic checks if anything is empty
        if (checkFields) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Please fill in all required fields!"
                    ));
            alert.show();
            return;
        }

        // validate all fields:

        // name must be letters and space only and shorter than 50
        if (!name.matches("^[a-zA-Z ]{1,50}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Name can only contain letters and spaces (max 50 characters).");
            alert.show();
            return;
        }

        // Phone number must be between 7 and 15 digits
        if (!phone.matches("\\d{7,15}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Phone number must be between 7 and 15 digits.");
            alert.show();
            return;
        }

        // Address must have letter and shorter than 50
        if (!address.isEmpty() && !address.matches("^(?=.*[a-zA-Z])[a-zA-Z0-9 ]{1,50}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Address must contain at least one letter and can include digits and spaces (max 50 characters).");
            alert.show();
            return;
        }

        //  Email must be less than 50 characters and follows a valid format
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]{1,50}@[A-Za-z0-9.-]{1,50}$") || email.length() >= 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format or email exceeds 50 characters!");
            alert.show();
            return;
        }

        // User ID must be exactly 9 digits.
        if (!userID.matches("\\d{3}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "User ID must be exactly 9 digits.");
            alert.show();
            return;
        }

        // Card Number must be exactly 16 digits.
        if (!cardNumber.matches("\\d{3}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Card Number must be exactly 16 digits.");
            alert.show();
            return;
        }

        // CVV must be exactly 3 digits.
        if (!cvv.matches("\\d{3}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "CVV must be exactly 3 digits.");
            alert.show();
            return;
        }


        //Validate the card expiry is not in the past
        LocalDate now = LocalDate.now();
        int currentYr = now.getYear();
        int currentMo = now.getMonthValue();

        if (year < currentYr) {
            // prints alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Card Expired (year < current year)!"
                    ));
            alert.show();
            return;
        } else if (year == currentYr && month < currentMo) {
            // prints alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Card Expired (month < current month)!"
                    ));
            alert.show();
            return;
        }

        // If we reach here => everything is valid
        System.out.println("All fields valid! Submitting...");
        System.out.printf(
                "Name: %s, Phone: %s, Address: %s%n",
                name, phone, address
        );
        System.out.printf(
                "ID: %s, Card#: %s, Expiry: %02d/%d, CVV: %s%n",
                userID, cardNumber, month, year, cvv
        );

        //If we got here, it means everything checks out, so we will submit the order.
        // -------  continue based on callerType  -------
        switch (callerType) {

            case "cart":
                //I know it's possible to make it shorter similar to the idea of my payload in messages, but I prefer to currently leave it as it and not to "compress" it for future changes that may be needed.
                //List<Integer> dishIds, List<String> Adaptations, String OrderType, int selectedBranch, Date orderDate, Double finalPrice, String name, String address, String phone, String userId, String cardNumber, int month, int year, String cvv
                try {
                    SimpleClient.getClient().sendAddOrder(OrderManage.getDishIds(),OrderManage.getAdaptations(),OrderManage.getOrderType(),OrderManage.getSelectedBranch(),OrderManage.getOrderDate(),OrderManage.getFinalPrice(),name,address,phone,userID,cardNumber,month,year,cvv);
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            String.format("Message: %s\n",
                                    "Order Submission Failed, please try again!"
                            ));
                    alert.show();
                    throw new RuntimeException(e);
                }
                // You can store this data or proceed.
                // e.g. App.setRoot("nextScene");
                break;


            case "orderTable":
                System.out.println("submitting a table order");
                SimpleClient.getClient().sendAddTableOrder(name, address, phone, userID, cardNumber, month, year, cvv);
                break;


            case "complaint":
                System.out.println("submitting a complaint");
                App.setRoot("primary");
                // Convert the current Date to UTC
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Date utcDate = calendar.getTime(); // This gives you the current date in UTC
                SimpleClient.getClient().sendComplaint(ComplaintController.getComplainText(), utcDate, ComplaintController.getBranchId(), name, "", phone, userID, cardNumber, month, year, cvv, email);
                break;

            default:
                System.out.println("error receiving callerType");
                App.setRoot("primary");
                break;
        }

    }

    @FXML
    private void onBack() throws IOException {
        // go back to different pages depending on the caller
        switch (callerType) {
            case "cart":
                App.setRoot("cart");
                break;
            case "orderTable":
                App.setRoot("orderTable");
                break;
            case "complaint":
                App.setRoot("complaint");
                break;
            default:
                System.out.println("Unknown caller type, proceeding with default behavior.");
                App.setRoot("primary");
                break;
        }
    }

    // will be called from the caller window
    public static void setCallerType(String callerType) {
        BuyerDetailsFormController.callerType = callerType;
    }

    public void updateUIBasedOnCallerType() {
        switch (callerType) {
            case "cart":
                totalCostLabel.setText(String.format("Total Cost: $%.2f", OrderManage.getFinalPrice()));
                break;
            case "orderTable":
                totalCostLabel.setText("Please note: If you cancel within the last hour, a ₪10 fee will be applied.");
                totalCostLabel.setStyle("-fx-font-size: 16; -fx-font-weight: normal;");
                header.setText("Reservation for " + TableOrderManage.getNumberOfGuests() + " guests on " + TableOrderManage.getDate() + " at " + TableOrderManage.getTime() + ".");
                break;
            case "complaint":
                totalCostLabel.setText("No charges will be made - this is only for a potential refund.");
                totalCostLabel.setStyle("-fx-font-size: 16; -fx-font-weight: normal;");
                addressField.setVisible(false);
                emailLabel.setVisible(true);
                emailText.setVisible(true);
                adressLabel.setVisible(false);
                header.setText("Personal Information Form");
                break;
            default:
                break;
        }
    }
}
