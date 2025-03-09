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

    private String name;
    private String creditCardNumber;

    // Constructors
    public Complaint() {
    }

    public Complaint(String complaint, Date date, String creditCardNumber, String name) {
        this.complaint = complaint;
        this.date = date;
        this.creditCardNumber = creditCardNumber;
        this.name = name;

    }

    // Getters and setters
    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
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