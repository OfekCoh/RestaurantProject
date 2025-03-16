package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Date;

public class ComplaintEnt implements Serializable {

    private String complaintText;
    private int id;
    private Date date;
    private String name;
    private String branchName;
    private int branchId;
    private int status; // state of complaint: 0 - waiting, 1 - handled by worker, 2 - handled automatically

    // add buyers details
    public ComplaintEnt(int id, String complaintText, Date date, String branchName, int branchId, String name, int status) {
        this.branchName = branchName;
        this.id = id;
        this.complaintText = complaintText;
        this.date = date;
        this.name = name;
        this.status = status;
        this.branchId = branchId;

    }

    // getters and setters
    public int getBranchId() { return branchId;}

    public String getBranchName() { return branchName;}

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public int getId() { return id;}

    public String getName() { return name;}

    public String getComplaintText() {
        return complaintText;
    }

    public Date getDate() {
        return date;
     }


}
