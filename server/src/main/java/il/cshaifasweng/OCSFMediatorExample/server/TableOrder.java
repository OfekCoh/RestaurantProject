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
    private int numberOfGuests;

    @Column(nullable = false)
    private int branchId;

    @ManyToMany
    @JoinTable(
            name = "table_order_tables",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<TableSchema> tables; // Table IDs

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time; // (starting time) till an hour and a half after

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int status; // 0=Pending/Completed, 1=Free Canceled, 2= Paid Canceled

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WhoSubmittedBy whoSubmitted; // ORDERED/מארחת

    @Embedded
    private BuyerDetails buyerDetails; // Embedding BuyerDetails to be stored in the same table

    public TableOrder() {
    }

    public TableOrder(int branchId, List<TableSchema> tables, String date, String time, int numberOfGuests, String location, int status, boolean buyerDetailsNeeded, BuyerDetails buyerDetails) {
        this.branchId = branchId;
        this.tables = tables;
        this.date = date;
        this.time = time;
        this.numberOfGuests = numberOfGuests;
        this.location = location;
        this.status = status;
        this.buyerDetails = buyerDetails;

        if(buyerDetailsNeeded) this.whoSubmitted = WhoSubmittedBy.ORDERED;
        else this.whoSubmitted = WhoSubmittedBy.HOSTESS;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
                ", numOfPeople=" + numberOfGuests +
                ", branchId=" + branchId +
                ", tables=" + (tables != null ? tables.size() : 0) +
                ", date=" + date +
                ", time=" + time +
                ", status=" + status +
                ", whoSubmitted=" + whoSubmitted +
                ", buyerDetails=" + buyerDetails +
                '}';
    }
}
