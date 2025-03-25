package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.LoginEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailLabel;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void loginClick(ActionEvent event) {
        String email = emailLabel.getText();

        //  Email must be less than 50 characters and follows a valid format
        if (!email.matches("^[A-Za-z0-9+_.-]{1,50}@[A-Za-z0-9.-]{1,50}$") || email.length() >= 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format or email exceeds 50 characters!");
            alert.show();
            return;
        }

        String password = passwordInput.getText();
        try {
            SimpleClient.getClient().sendLoginCommand(email,password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void backClick(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
//        System.out.println("onMenuEvent called! Updating table...");
        Platform.runLater(() -> {
            try {
                App.setRoot("primary");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void initialize() {
        // Register to the eventBus
        EventBus.getDefault().register(this);
    }

}
