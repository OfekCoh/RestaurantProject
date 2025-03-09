package il.cshaifasweng.OCSFMediatorExample.entities;

import java.util.Date;

public class ComplaintEnt {

    private String complaintText;
    private int id;
    private Date date;
    private String name;
    private String creditCardNumber;


    // add buyers details
    public ComplaintEnt(int id, String complaintText, Date date, String name, String creditCardNumber) {
        this.id = id;
        this.complaintText = complaintText;
        this.date = date;
        this.name = name;
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getName() {
        return name;
    }

    public String getComplaintText() {
        return complaintText;
    }

     public Date getDate() {
        return date;
     }


}
