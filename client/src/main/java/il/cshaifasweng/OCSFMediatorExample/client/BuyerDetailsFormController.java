package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.stream.IntStream;

public class BuyerDetailsFormController {

    private static String callerType; // who called me.
    @FXML
    private Label header;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;

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
    @FXML
    private Label totalCostLabel;

    @FXML
    private Button submitButton;
    @FXML
    private Button backButton;

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

        updateUIBasedOnCallerType(); // change the bottom label depending on the caller type
    }

    @FXML
    private void onSubmit() throws IOException {
        // Gather personal info
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Payment info
        String userID = idField.getText().trim();
        String cardNumber = cardNumberField.getText().trim();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        String cvv = cvvField.getText().trim();

        // Basic checks if anything is empty
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || userID.isEmpty() || cardNumber.isEmpty() || cvv.isEmpty() || month == null || year == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Please fill in all required fields!"
                    ));
            alert.show();
//            System.out.println("Please fill in all required fields!");
            return;
        }

        // Validate numeric phone, ID, cardNumber, CVV
        //    You can do more advanced checks as needed.
        try {
            Long.parseLong(phone); // phone must be numeric
            Long.parseLong(userID); // ID numeric
            Long.parseLong(cardNumber); // cardNumber numeric
            Integer.parseInt(cvv); // CVV numeric
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Phone/ID/CardNumber/CVV must be numeric!"
                    ));
            alert.show();
//            System.out.println("Phone/ID/CardNumber/CVV must be numeric!");
            return;
        }

        //Validate the card expiry is not in the past
        LocalDate now = LocalDate.now();
        int currentYr = now.getYear();
        int currentMo = now.getMonthValue();

        if (year < currentYr) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Card Expired (year < current year)!"
                    ));
            alert.show();
//            System.out.println("Card Expired (year < current year)!");
            return;
        } else if (year == currentYr && month < currentMo) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format("Message: %s\n",
                            "Card Expired (month < current month)!"
                    ));
            alert.show();
//            System.out.println("Card Expired (month < current month)!");
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
                //I know it's possible to make it shorter similiar to the idea of my paylod in messages, but I prefer to currently leave it as it and not to "compress" it for future changes that may be needed.
                //List<Integer> dishIds, List<String> Adaptaions, String OrderType, int selectedBranch, Date orderDate, Double finalPrice, String name, String address, String phone, String userId, String cardNumber, int month, int year, String cvv
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
                SimpleClient.getClient().sendComplaint(ComplaintController.getComplainText(), new Date(), name, address, phone, userID, cardNumber, month, year, cvv);
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
                totalCostLabel.setText("Please note: If you cancel within the last hour, a $10 fee will be applied.");
                totalCostLabel.setStyle("-fx-font-size: 16; -fx-font-weight: normal;");
                header.setText("Reservation for " + TableOrderManage.getNumberOfGuests() + " guests on " + TableOrderManage.getDate() + " at " + TableOrderManage.getTime() + ".");
                break;
            case "complaint":
                totalCostLabel.setText("No charges will be made - this is only for a potential refund.");
                totalCostLabel.setStyle("-fx-font-size: 16; -fx-font-weight: normal;");
                break;
            default:
                break;
        }
    }
}
