package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.IntStream;

public class BuyerDetailsFormController {

    private static String previousFXML; // to determine which order to perform
    public static void setPreviousFXML(String previousFXML) {
        BuyerDetailsFormController.previousFXML = previousFXML;
    }
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
        if(previousFXML != null && previousFXML.equals("order")) {
            totalCostLabel.setText(String.format("Total Cost: $%.2f", OrderManage.getFinalPrice()));
        }

    }

    @FXML
    private void onSubmit() {
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

        //I know it's possible to make it shorter similiar to the idea of my paylod in messages, but I prefer to currently leave it as it and not to "compress" it for future changes that may be needed.
        //List<Integer> dishIds, List<String> Adaptaions, String OrderType, int selectedBranch, Date orderDate, Double finalPrice, String name, String address, String phone, String userId, String cardNumber, int month, int year, String cvv
        try {
            //decide what happen on submit based on previous fxml
            switch (previousFXML){
                case "order":
                    SimpleClient.getClient().sendAddOrder(OrderManage.getDishIds(),OrderManage.getAdaptations(),OrderManage.getOrderType(),OrderManage.getSelectedBranch(),OrderManage.getOrderDate(),OrderManage.getFinalPrice(),name,address,phone,userID,cardNumber,month,year,cvv);
                    break;

                case "complaint":
                    SimpleClient.getClient().sendComplaint(ComplaintController.getComplainText(), new Date(), name, address, phone, userID, cardNumber, month, year, cvv);
                break;

                default:
                    break;

            }
            App.setRoot("primary");
        } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                String.format("Message: %s\n",
                        "Order Submission Failed, please try again!"
                ));
        alert.show();
        throw new RuntimeException(e);
        }
    }

    @FXML
    private void onBack() throws IOException {
        // decide which page to return on back
        switch (previousFXML){
            case "order":
                App.setRoot("cart");
                break;

            case "complaint":
                App.setRoot("complaint");
                break;

            default:
                break;
        }

    }
}
