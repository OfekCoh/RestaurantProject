package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuChangeEnt;

import java.util.List;

public class MenuChangeEvent {
    private List<MenuChangeEnt> menuChangesList;

    public MenuChangeEvent(List<MenuChangeEnt> list) {
        this.menuChangesList = list;
    }

    public List<MenuChangeEnt> getMenuChangesList() {
        return menuChangesList;
    }

    public void setMenuChangesList(List<MenuChangeEnt> menuChangesList) {
        this.menuChangesList = menuChangesList;
    }
}
