package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.fxml.FXML;
import java.io.IOException;


public class PrimaryController {

    @FXML
    private void switchToMenu() throws IOException {
        SimpleClient.getClient().sendToServer("getMenu");
        App.setRoot("menuView");
    }

    @FXML
    void initialize(){
        try {
            SimpleClient.getClient().sendToServer("add client");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}