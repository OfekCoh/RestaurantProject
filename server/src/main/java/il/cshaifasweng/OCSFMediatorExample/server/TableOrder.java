package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "table_orders")
public class TableOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(nullable = false)
    private int numOfPeople; // מספר סועדים

    @Column(nullable = false)
    private int branchId;


    //I'm not sure if we want an int list that will represent table id's , much easier.
    // If one order can span multiple tables, use ManyToMany:
    @ManyToMany
    @JoinTable(
            name = "table_order_tables",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<TableSchema> tables; // Table IDs

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int status; // 0=Pending, 1=Free-Canceled, 2=Partly-Refund, 3=No refund, 4=Completed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WhoSubmittedBy whoSubmitted; // ORDERED/מארחת

    // Embedding BuyerDetails to be stored in the same table
    @Embedded
    private BuyerDetails buyerDetails;

    public TableOrder() {
    }

    public TableOrder(int numOfPeople, int branchId, List<TableSchema> tables, LocalDateTime startDate, LocalDateTime endDate, int status, WhoSubmittedBy whoSubmitted, BuyerDetails buyerDetails) {
        this.numOfPeople = numOfPeople;
        this.branchId = branchId;
        this.tables = tables;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.whoSubmitted = whoSubmitted;
        this.buyerDetails = buyerDetails;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public List<TableSchema> getTables() {
        return tables;
    }

    public void setTables(List<TableSchema> tables) {
        this.tables = tables;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BuyerDetails getBuyerDetails() {
        return buyerDetails;
    }

    public void setBuyerDetails(BuyerDetails buyerDetails) {
        this.buyerDetails = buyerDetails;
    }

    /**
     * Converts the integer 'status' to a human-readable string.
     */
    public String getStatusDescription() {
        switch (status) {
            case 0:
                return "pending";
            case 1:
                return "Free-Canceled";
            case 2:
                return "Partly-Refund";
            case 3:
                return "No refund";
            case 4:
                return "completed";
            default:
                return "Unknown status (" + status + ")";
        }
    }

    public WhoSubmittedBy getWhoSubmitted() {
        return whoSubmitted;
    }

    public void setWhoSubmitted(WhoSubmittedBy whoSubmitted) {
        this.whoSubmitted = whoSubmitted;
    }

    @Override
    public String toString() {
        return "TableOrder{" +
                "orderId=" + orderId +
                ", numOfPeople=" + numOfPeople +
                ", branchId=" + branchId +
                ", tables=" + (tables != null ? tables.size() : 0) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", whoSubmitted=" + whoSubmitted +
                ", buyerDetails=" + buyerDetails +
                '}';
    }
}
