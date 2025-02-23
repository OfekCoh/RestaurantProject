package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;

import java.util.List;

public class DishEvent {
    private List<DishEnt> dishList;

    public DishEvent(List<DishEnt> list) {
        this.dishList =  list;
    }

    public List<DishEnt> getDishList() {
        return dishList;
    }
}


