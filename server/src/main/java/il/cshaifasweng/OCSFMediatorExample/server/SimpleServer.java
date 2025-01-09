package il.cshaifasweng.OCSFMediatorExample.server;


import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuEnt;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import static il.cshaifasweng.OCSFMediatorExample.server.DatabaseServer.updatePriceForDish;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private DatabaseServer databaseServer;

    public SimpleServer(int port, String databasePassword) {
        super(port);
        DatabaseServer.password = databasePassword;
        // create the database
        databaseServer = new DatabaseServer(databasePassword);
    }

    // from here i didnt change anything from the given code
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String msgString = msg.toString();
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
                System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.startsWith("add client")) {
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            try {
                client.sendToClient("client added successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (msgString.startsWith("remove client")) {
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        } else if (msgString.startsWith("getMenu")) {
            try {
                List<DishEnt> dishes = DatabaseServer.getMenu();
                MenuEnt menu = new MenuEnt();
                menu.setDishes(dishes);
                sendToAllClients(menu);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (msgString.startsWith("UpdatePrice")) {
            String[] splitMessage = msgString.split(" ");
            int id = Integer.parseInt(splitMessage[1]);
            int price = Integer.parseInt(splitMessage[3]);

            //We update the price
            //We send update success
			//We send the menu again for the client.
            try {
                updatePriceForDish(id,price);
                List<DishEnt> dishes = DatabaseServer.getMenu();
                MenuEnt menu = new MenuEnt();
                menu.setDishes(dishes);
                sendToAllClients(menu);
                //Successful Update
                Warning warning = new Warning("Successfully updated price!");
                client.sendToClient(warning);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendToAllClients(String message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
