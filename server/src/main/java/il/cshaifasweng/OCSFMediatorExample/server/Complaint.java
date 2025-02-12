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

    // Embeds BuyerDetails into the complaint table
    @Embedded
    private BuyerDetails buyerDetails;

    // Constructors
    public Complaint() {
    }

    public Complaint(String complaint, Date date, BuyerDetails buyerDetails) {
        this.complaint = complaint;
        this.date = date;
        this.buyerDetails = buyerDetails;
    }

    // Getters and setters
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

    public BuyerDetails getBuyerDetails() {
        return buyerDetails;
    }

    public void setBuyerDetails(BuyerDetails buyerDetails) {
        this.buyerDetails = buyerDetails;
    }
}