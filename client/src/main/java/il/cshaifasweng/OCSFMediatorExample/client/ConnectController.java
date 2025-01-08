package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;


public class ConnectController {

    @FXML
    private TextField ipTF;

    @FXML
    private TextField portTF;

    @FXML
    void connectButton(ActionEvent event) {
        SimpleClient.ip = ipTF.getText();
        SimpleClient.port = Integer.parseInt(portTF.getText());
        client = SimpleClient.getClient();
        try{
            client.openConnection();
            App.setRoot("primary");
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
