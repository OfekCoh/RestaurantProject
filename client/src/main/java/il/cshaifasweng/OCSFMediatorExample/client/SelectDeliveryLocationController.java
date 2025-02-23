package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SelectDeliveryLocationController {
    private static String selectedOrderType;

    @FXML
    private ComboBox<BranchEnt> branchComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    public static void setSelectedOrderType(String selectedOrderType) {
        SelectDeliveryLocationController.selectedOrderType = selectedOrderType;
    }

    @FXML
    public void initialize() {
        // Initially disable the date picker and timeComboBox until selections are made.
        datePicker.setDisable(true);
        timeComboBox.setDisable(true);
        timeComboBox.setPromptText("Select branch and date");

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
            return;
        }

        // If the selected date is today, adjust the starting time to avoid past time slots.
        LocalTime now = LocalTime.now();
        LocalTime startTime = openTime;
        if (date.equals(LocalDate.now()) && now.isAfter(openTime)) {
            int minute = now.getMinute();
            int remainder = minute % 30;
            if (remainder != 0) {
                now = now.plusMinutes(30 - remainder);
            }
            if (now.isAfter(openTime)) {
                startTime = now;
            }
        }

        // Generate 30-minute time slots from startTime until at least 30 minutes before closing.
        List<String> timeSlots = new ArrayList<>();
        LocalTime slot = startTime;
        while (!slot.isAfter(closeTime.minusMinutes(30))) {
            timeSlots.add(slot.format(timeFormatter));
            slot = slot.plusMinutes(15);
        }

        // If no valid time slots were generated, disable the ComboBox with the proper prompt.
        if (timeSlots.isEmpty()) {
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Closed");
            timeComboBox.setDisable(true);
        } else {
            timeComboBox.setItems(FXCollections.observableArrayList(timeSlots));
            timeComboBox.setPromptText("Select a time slot");
            timeComboBox.setDisable(false);
        }
    }

    @FXML
    void goBack(ActionEvent event) throws Exception {
        App.setRoot("orderSelectionType");
    }

    @FXML
    private void handleConfirm(ActionEvent event) throws IOException {
        BranchEnt branch = branchComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        String time = timeComboBox.getSelectionModel().getSelectedItem();

        // Validate that all selections are made.
        if (branch == null) {
            showAlert("Branch Not Selected", "Please select a branch.");
            return;
        }
        if (date == null) {
            showAlert("Date Not Selected", "Please select a date.");
            return;
        }
        if (time == null || time.isEmpty() || time.equals("Closed") || time.equals("Branch Closed")) {
            showAlert("Time Slot Not Selected", "Please select a valid time slot.");
            return;
        }

        System.out.println("Selected branch: " + branch);
        System.out.println("Selected date: " + date);
        System.out.println("Selected time: " + time);
        System.out.println("Selected Delivery type: "+selectedOrderType);
        // Further processing...
        DishSelectionController.setSelectedBranch(branch.getId());
        DishSelectionController.setSelectedMode("order");
        App.setRoot("dishSelection");
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
