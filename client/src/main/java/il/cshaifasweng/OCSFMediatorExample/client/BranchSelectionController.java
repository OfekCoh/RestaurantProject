package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;

public class BranchSelectionController {

    private static String next = "dish"; // Which fxml to move after selection

    private List<BranchEnt> branches;

    private static final String[] DAYS_OF_WEEK = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    @FXML
    private Button backButton;

    @FXML
    private VBox branchContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text headlineText;



    public static void setNextWindow(String nextWindow){
        next = nextWindow;
    }


    @FXML
    void backClick(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    public void setBranches(List<BranchEnt> branches) {
        this.branches = branches;
        populateBranchList();
    }

    private void populateBranchList() {
        branchContainer.getChildren().clear(); // Clear previous content

        for (BranchEnt branch : branches) {
            String formattedHours = formatOpeningHours(branch.getOpeningHours());

            Button branchButton = new Button(
                    branch.getBranchName() + "\n" +
                            "📍 " + branch.getLocation() + "\n⏰ Opening Time:\n" +
                            formattedHours
            );

            branchButton.setOnAction(e -> onBranchSelected(branch.getId()));
            branchButton.setMaxWidth(Double.MAX_VALUE);
            branchButton.setStyle(
                    "-fx-font-size: 16px; " +
                            "-fx-padding: 15px; " +
                            "-fx-background-color: #F5F5F5; " +
                            "-fx-border-color: #B0BEC5; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-text-alignment: center;" +
                            "-fx-font-weight: bold;"
            );

            // Add some spacing
            VBox.setMargin(branchButton, new javafx.geometry.Insets(10, 20, 10, 20));

            branchContainer.getChildren().add(branchButton);
        }
    }

    private String formatOpeningHours(String[] openingHours) {
        if (openingHours == null || openingHours.length != 7) {
            return "Opening hours unavailable";
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            formatted.append(DAYS_OF_WEEK[i]).append(": ").append(openingHours[i]).append("\n");
        }
        return formatted.toString().trim();
    }

    private void onBranchSelected(int branchId) {
        System.out.println("Branch selected: " + branchId);
        String branchName = branches.stream()
                .filter(branch -> branch.getId() == branchId)
                .map(BranchEnt::getBranchName)
                .findFirst()
                .orElse("Unknown Branch");


        try{
            if(next.equals("restaurant map")){
                // check if the branch is closed
                BranchEnt selectedBranch = branches.stream().filter(branch -> branch.getId() == branchId).findFirst().orElse(null); // get branch
                int dayIndex = LocalDate.now().getDayOfWeek().getValue() % 7; // get today's day
                String[] openingHours = selectedBranch.getOpeningHours(); // get opening hours for all week
                String todayHours = openingHours[dayIndex]; // get opening hours for today
                String[] hours = todayHours.split("-"); // splits 08:00-16:00 to [08:00,16:00]
                LocalTime closingTime = LocalTime.parse(hours[1].trim(), DateTimeFormatter.ofPattern("HH:mm")); // gets closing time
                LocalTime now = LocalTime.now();

                // if closed show alert
                if (now.isAfter(closingTime)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Branch is closed!");
                    alert.show();
                    return;
                }

                // else continue to map
                RestaurantMapController.setBranchId(branchId);
                App.setRoot("restaurantMap");
            }
            if(next.equals("dish selection")){
                DishSelectionController.setSelectedBranchName(branchName);
                DishSelectionController.setSelectedMode("view");
                DishSelectionController.setSelectedBranch(branchId);
                App.setRoot("dishSelection");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Subscribe
    public void onBranchEvent(BranchEvent event) {
//        System.out.println("onMenuEvent called! Updating table...");
        Platform.runLater(() -> {
            setBranches(event.getBranchList());
        });
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        if(next.equals("restaurant map")){
            headlineText.setText("Restaurant Map - Branch Selection:");
        }
        else if(next.equals("dish selection")){
            headlineText.setText("Menu - Branch Selection:");
        }
        setBranches(SimpleClient.BranchList);
    }
}
