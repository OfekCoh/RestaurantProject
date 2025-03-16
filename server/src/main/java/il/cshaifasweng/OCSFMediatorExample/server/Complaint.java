package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int complaintId;

    private String complaint;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Embedded // BuyersDetails fields will be stored in the complaints table
    private BuyerDetails buyerDetails;

    private int status; // state of complaint: 0 - waiting, 1 - handled by worker, 2 - handled automatically
    private double refund; // how much was refunded
    private int branchId;
    private String email;

    // Constructor
    public Complaint() {
    }

    public Complaint(String complaint, Date date, int branchId, BuyerDetails buyerDetails, String email) {
        this.branchId = branchId;
        this.complaint = complaint;
        this.date = date;
        this.buyerDetails = buyerDetails;
        this.status = 0;
        this.refund = 0;
        this.email = email;
    }

    // Getters and setters
    public void setEmail(String email) { this.email = email;}

    public String getEmail() { return email;}

    public int getBranchId() { return branchId;}

    public void setBranchName(int branchID) { this.branchId = branchId;}

    public double getRefund() { return refund;}

    public void setRefund(double refund) { this.refund = refund;}

    public int getStatus() { return this.status; }

    public void setStatus(int status) { this.status = status; }

    public void setBuyerDetails(BuyerDetails buyerDetails) { this.buyerDetails = buyerDetails;}

    public BuyerDetails getBuyerDetails() { return buyerDetails;}

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}