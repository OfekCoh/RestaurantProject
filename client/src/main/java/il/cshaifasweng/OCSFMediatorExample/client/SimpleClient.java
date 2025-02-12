package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuEnt;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuEnt;
import java.util.List;

public class SimpleClient extends AbstractClient {

    public static SimpleClient client = null;
    public static int port = 3000;
    public static String ip = "localhost";
    public static List<DishEnt> DishList;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if(msg.getClass().equals(MenuEnt.class)) {
            System.out.println("Menu Message: "+msg);
            MenuEnt menu = (MenuEnt) msg;
            DishList=menu.getListDishes();
            for(DishEnt dishEnt : DishList) {
                System.out.println(dishEnt.toString());
            }

            System.out.println(DishList.toArray().toString());
            EventBus.getDefault().post(new MenuEvent((MenuEnt) msg));
        }
        else if (msg.getClass().equals(Warning.class)) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient(ip, port);
        }
        return client;
    }

}
