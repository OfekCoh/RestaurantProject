package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.TableEnt;

import java.util.List;

public class RestaurantMapEvent {


    private List<TableEnt> takenTables;
    private List<TableEnt> availableTables;
    private int branchId;

    public RestaurantMapEvent(List<TableEnt> takenTables, List<TableEnt> availableTables, int branchId) {
        this.takenTables = takenTables;
        this.availableTables = availableTables;
        this.branchId = branchId;
    }

    public List<TableEnt> getTakenTables() {
        return takenTables;
    }

    public List<TableEnt> getAvailableTables() {
        return availableTables;
    }

    public int getBranchId() {
        return branchId;
    }


}
