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
    private int refund; // how much was refunded

    // Constructors
    public Complaint() {
    }

    public Complaint(String complaint, Date date, BuyerDetails buyerDetails) {
        this.complaint = complaint;
        this.date = date;
        this.buyerDetails = buyerDetails;
        this.status = 0;
        this.refund = 0;
    }

    // Getters and setters

    public int getRefund() { return refund;}

    public void setRefund(int refund) { this.refund = refund;}

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