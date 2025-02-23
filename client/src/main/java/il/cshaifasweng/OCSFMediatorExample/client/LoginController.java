package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
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
