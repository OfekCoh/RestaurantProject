package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.LoginEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.LogoutEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.WarningEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Stage primaryStage;
    private static BorderPane rootLayout;
    private static ToolBar globalToolBar;

    private static Scene scene;
    public SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        rootLayout = new BorderPane();


        globalToolBar = new ToolBar();
        Button disconnectButton = new Button("Disconnect");
        disconnectButton.setOnAction(e -> handleDisconnect());
        globalToolBar.getItems().add(disconnectButton);
        globalToolBar.setVisible(false); //Initially hidden
        rootLayout.setTop(globalToolBar);

        //Register for EventBus updates (login/logout/Warning)
        EventBus.getDefault().register(this);

        // Load the initial view (PrimaryController)

//        primaryStage.setTitle("Resturant");
//        scene = new Scene(loadFXML("connect"), 1000, 600);
        scene = new Scene(rootLayout, 1000, 600);
//        setRoot("primary");
        primaryStage.setScene(scene);
        setRoot("connect");

        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        rootLayout.setCenter(loadFXML(fxml));
//        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void handleDisconnect() {
        try {
            SimpleClient.getClient().sendLogoutCommand(SimpleClient.userID);
            SimpleClient.userID = -1;
            SimpleClient.ruleID = -1;
            globalToolBar.setVisible(false);
            setRoot("primary");
            EventBus.getDefault().post(new LogoutEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLoginSuccess(LoginEvent event) {
        Platform.runLater(() -> {
            SimpleClient.userID = event.getUserID();
            SimpleClient.ruleID = event.getRuleID();
            globalToolBar.setVisible(true);
        });
    }

    @Subscribe
    public void onLogout(LogoutEvent event) {
        Platform.runLater(() -> {
            SimpleClient.userID = -1;
            SimpleClient.ruleID = -1;
            globalToolBar.setVisible(false); //Hide toolbar when logged out
        });
    }


    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
        if (client != null) {
            client.sendToServer("remove client");
            client.closeConnection();
        }
        //Logout in case user is logged in (when we close the app).
        if(SimpleClient.userID!=-1){
            SimpleClient.getClient().sendLogoutCommand(SimpleClient.userID);
        }
		super.stop();
        Platform.exit(); // Forcefully exit JavaFX platform
        System.exit(0);  // Ensure the JVM shuts down
	}

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.INFORMATION,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});

    }

	public static void main(String[] args) {
        launch();
    }

    // so we can send parameters through fxml
    public static BorderPane getRootLayout() {
        return rootLayout;
    }

}