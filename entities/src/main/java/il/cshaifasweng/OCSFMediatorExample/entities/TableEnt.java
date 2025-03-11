package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class TableEnt implements Serializable {
    private static final long serialVersionUID = 1L;

    private int tableId;
    private int branchId;
    private int numberOfDiners;
    private String location; // Change from List<String> to String[]

    public TableEnt() {
    }

    public TableEnt(int tableId, int branchId, int numberOfDiners, String location) {
        this.tableId = tableId;
        this.branchId = branchId;
        this.numberOfDiners = numberOfDiners;
        this.location = location;

    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getNumberOfDiners() {
        return numberOfDiners;
    }

    public void setNumberOfDiners(int numberOfDiners) {
        this.numberOfDiners = numberOfDiners;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
