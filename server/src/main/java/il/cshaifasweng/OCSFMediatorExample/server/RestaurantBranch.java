package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant_branches")
public class RestaurantBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String branchName; // e.g. "Downtown Branch"

    @Column(nullable = false)
    private String location;   // e.g. "123 Main St."

    /**
     * Stores opening hours as a list of 7 strings (one for each day).
     * Example usage: openingHours.get(0) -> Monday's hours, etc.
     *
     * This creates a separate table "branch_opening_hours" under the hood,
     * but from your perspective, it's just a List<String> in the entity.
     */
    @ElementCollection
    @CollectionTable(
            name = "branch_opening_hours",
            joinColumns = @JoinColumn(name = "branch_id")
    )
    @Column(name = "day_hours")
    private List<String> openingHours;
    // Example content: ["08:00-16:00","08:00-16:00","08:00-16:00","08:00-16:00","08:00-22:00","09:00-22:00","Closed"]

    /**
     * One branch can have multiple tables.
     * We assume TableSchema has a 'branch' field annotated with @ManyToOne.
     */
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TableSchema> tables;

    public RestaurantBranch() {}

    public RestaurantBranch(String branchName, String location, List<String> openingHours) {
        this.branchName = branchName;
        this.location = location;
        this.openingHours = openingHours;
    }

    public int getId() {
        return id;
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

    public List<String> getOpeningHours() {
        return openingHours;
    }
    public void setOpeningHours(List<String> openingHours) {
        this.openingHours = openingHours;
    }

    public List<TableSchema> getTables() {
        return tables;
    }
    public void setTables(List<TableSchema> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "RestaurantBranch{" +
                "id=" + id +
                ", branchName='" + branchName + '\'' +
                ", location='" + location + '\'' +
                ", openingHours=" + openingHours +
                ", tablesCount=" + (tables != null ? tables.size() : 0) +
                '}';
    }
}