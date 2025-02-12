package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;

@Entity
@Table(name = "tables")
public class TableSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tableId; // Unique ID for each table

    @Column(nullable = false)
    private int branchId; // Branch number where the table is located

    @Column(nullable = false)
    private int numberOfDiners; // Number of seats

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType location; // Indoor or Outdoor seating

    public TableSchema() {}

    public TableSchema(int branchId, int numberOfDiners, LocationType location) {
        this.branchId = branchId;
        this.numberOfDiners = numberOfDiners;
        this.location = location;
    }

    public int getTableId() { return tableId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public int getNumberOfDiners() { return numberOfDiners; }
    public void setNumberOfDiners(int numberOfDiners) { this.numberOfDiners = numberOfDiners; }

    public LocationType getLocation() { return location; }
    public void setLocation(LocationType location) { this.location = location; }

    @Override
    public String toString() {
        return "TableEntity{" + "tableId=" + tableId + ", branchId=" + branchId +
                ", numberOfDiners=" + numberOfDiners + ", location=" + location + "}";
    }
}

//Get specific branches tables
//List<TableEntity> tables = session.createQuery("FROM TableEntity WHERE branchId = :branchId", TableEntity.class)
//                                  .setParameter("branchId", 101)
//                                  .getResultList();
//tables.forEach(System.out::println);

//Adding new table example:
//TableEntity table = new TableEntity(101, 4, LocationType.OUTDOOR);
//session.save(table);