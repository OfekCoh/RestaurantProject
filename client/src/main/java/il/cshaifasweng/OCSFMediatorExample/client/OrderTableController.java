package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.stream.IntStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderTableController {

    @FXML
    private Button confirmButton;

    @FXML
    private ComboBox<BranchEnt> branchComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Integer> numberOfGuestsComboBox;

    @FXML
    private ComboBox<String> sittingLocationComboBox1;

    @FXML
    private ComboBox<String> timeComboBox;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @FXML
    public void initialize() {
        // Initially disable ComboBoxes until selections are made.
        datePicker.setDisable(true);
        timeComboBox.setDisable(true);
        confirmButton.setDisable(true);
        numberOfGuestsComboBox.setDisable(true);
        sittingLocationComboBox1.setDisable(true);

        // add prompt texts
        timeComboBox.setPromptText("Select branch and date");
        numberOfGuestsComboBox.setPromptText("Select Number Of Guests");
        sittingLocationComboBox1.setPromptText("Where would you like to sit?");

        // Set custom cell factory to display "branch id - branch name"
        branchComboBox.setCellFactory(lv -> new ListCell<BranchEnt>() {
            @Override
            protected void updateItem(BranchEnt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getId() + " - " + item.getBranchName());
            }
        });
        branchComboBox.setButtonCell(new ListCell<BranchEnt>() {
            @Override
            protected void updateItem(BranchEnt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getId() + " - " + item.getBranchName());
            }
        });

        // Populate the branchComboBox with branches from your client data.
        List<BranchEnt> branches = new ArrayList<>();
        branches.addAll(SimpleClient.BranchList);
        branchComboBox.setItems(FXCollections.observableArrayList(branches));

        // Disable past dates on the DatePicker.
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });

        // When a branch is selected, enable the DatePicker and update the time slots.
        branchComboBox.valueProperty().addListener((obs, oldBranch, newBranch) -> {
            if (newBranch != null) {
                datePicker.setDisable(false);
            } else {
                datePicker.setDisable(true);
            }
            updateTimeComboBox();
        });

        // When a date is selected, update the time selection.
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateTimeComboBox();
        });

        // when time is selected, enable confirm button and more options
        timeComboBox.valueProperty().addListener((obs, oldTime, newTime) -> {
            if (newTime != null) {
                numberOfGuestsComboBox.setDisable(false);
                sittingLocationComboBox1.setDisable(false);
                confirmButton.setDisable(false);

                // add choices of 1-20 people
                numberOfGuestsComboBox.setItems(FXCollections.observableArrayList(
                        IntStream.rangeClosed(1, 20).boxed().toList()
                ));

                // add choices to seats
                sittingLocationComboBox1.setItems(FXCollections.observableArrayList("Doesnt Matter", "Inside", "Outside"));

                // Set default selections if none were chosen already
                if (sittingLocationComboBox1.getValue() == null) {
                    sittingLocationComboBox1.setValue("Doesnt Matter");
                }
                if (numberOfGuestsComboBox.getValue() == null) {
                    numberOfGuestsComboBox.setValue(2);
                }

            } else {  // if time wasnt selected
                numberOfGuestsComboBox.setDisable(true);
                sittingLocationComboBox1.setDisable(true);
                confirmButton.setDisable(true);
            }
        });

    }


    /**
     * Updates the timeComboBox based on the selected branch's opening hours for the selected date.
     * If the branch is closed on that day or (if today) already closed, the ComboBox shows only a prompt,
     * and is disabled.
     */
    private void updateTimeComboBox() {
        BranchEnt branch = branchComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();

        timeComboBox.getItems().clear();

        // If no branch or date is selected, disable the ComboBox with an appropriate prompt.
        if (branch == null || date == null) {
            timeComboBox.setPromptText("Select branch and date");
            timeComboBox.setDisable(true);
            confirmButton.setDisable(true);

            return;
        }

        // Ensure the timeComboBox is visible.
        timeComboBox.setDisable(false);

        // Map the day to the correct index assuming the openingHours array starts with Sunday.
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int index = dayOfWeek.getValue() % 7; // Sunday (7 % 7 = 0), Monday (1 % 7 = 1), etc.
        String hours = branch.getOpeningHours()[index];

        // If the branch is marked as "Closed" for that day.
        if (hours.equalsIgnoreCase("Closed")) {
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Branch Closed");
            timeComboBox.setDisable(true);
            confirmButton.setDisable(true);
            return;
        }

        String[] parts = hours.split("-");
        LocalTime openTime = LocalTime.parse(parts[0], timeFormatter);
        LocalTime closeTime = LocalTime.parse(parts[1], timeFormatter);

        // If today and the current time is after the closing time, show a prompt and disable the ComboBox.
        if (date.equals(LocalDate.now()) && LocalTime.now().isAfter(closeTime)) {
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Closed");
            timeComboBox.setDisable(true);
            confirmButton.setDisable(true);
            return;
        }

        // If the selected date is today, adjust the starting time to avoid past time slots.
        LocalTime now = LocalTime.now();
        LocalTime startTime = openTime.plusMinutes(15);
        if (date.equals(LocalDate.now()) && now.isAfter(startTime)) {
            int minute = now.getMinute();
            int remainder = minute % 30;
            if (remainder != 0) {
                now = now.plusMinutes(30 - remainder);
            }
            if (now.isAfter(startTime)) {
                startTime = now;
            }
        }

        // Generate 15-minute time slots from startTime until at least 60 minutes before closing.
        List<String> timeSlots = new ArrayList<>();
        LocalTime slot = startTime;
        while (!slot.isAfter(closeTime.minusMinutes(60))) {
            timeSlots.add(slot.format(timeFormatter));
            slot = slot.plusMinutes(15);
        }

        // If no valid time slots were generated, disable the ComboBox with the proper prompt.
        if (timeSlots.isEmpty()) {
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Closed");
            timeComboBox.setDisable(true);
            confirmButton.setDisable(true);
        } else {
            timeComboBox.setItems(FXCollections.observableArrayList(timeSlots));
            timeComboBox.setPromptText("Select a time slot");
            timeComboBox.setDisable(false);
        }
    }

    @FXML
    void goBack(ActionEvent event) throws Exception {
        App.setRoot("primary");
    }

    @FXML
    void handleConfirm(ActionEvent event) throws Exception{
        // check if theres room
        // if yes- go to payment
        //
        // if no- show other hours available in that day,
        // or ask to change to a different branch with same parameters


        if(SimpleClient.userID != -1) {   // if it's a worker
            System.out.println("Worker gave costumers a table");
            App.setRoot("primary");
        }
        else {  // if it's a costumer
            System.out.println("A costumer wants to get a table, sending him to fill payments");
            BuyerDetailsFormController.setCallerType("orderTable");  // Pass caller "cart" to buyerform
            App.setRoot("buyerForm");
        }
    }

    /**
     * Utility method to display an alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
