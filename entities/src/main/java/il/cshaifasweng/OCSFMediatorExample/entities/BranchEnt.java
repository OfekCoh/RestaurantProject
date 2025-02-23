package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BranchEnt implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String branchName;
    private String location; // Change from List<String> to String[]
    private String[] openingHours;
    private List<TableEnt> tables;

    public BranchEnt() {}

    public BranchEnt(int id, String branchName){
        this.id = id;
        this.branchName = branchName;
    }

    public BranchEnt(int id, String branchName, String location, String[] openingHours, List<TableEnt> tables) {
        this.id = id;
        this.branchName = branchName;
        this.location = location;  // a String[]
        this.openingHours = openingHours;
        this.tables = tables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String[] openingHours) {
        this.openingHours = openingHours;
    }

    public List<TableEnt> getTables() {
        return tables;
    }

    public void setTables(List<TableEnt> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "BranchEnt{" +
                "id=" + id +
                ", branchName='" + branchName + '\'' +
                ", location='" + location + '\'' +
                ", openingHours=" + Arrays.toString(openingHours) +
                ", tables=" + tables +
                '}';
    }
}
