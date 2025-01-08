package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuEnt;
import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;

public class MenuEvent {
    private MenuEnt menu;

    public MenuEvent(MenuEnt menu) {
        this.menu =  menu;
    }

    public MenuEnt getMenu() {
        return menu;
    }
}


