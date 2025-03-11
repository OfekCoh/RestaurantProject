package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.ComplaintEnt;

import java.util.List;

public class ComplaintEvent {
    private List<ComplaintEnt> complaints;

    public ComplaintEvent(List<ComplaintEnt> list) {
        this.complaints = list;
    }

    public List<ComplaintEnt> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<ComplaintEnt> complaints) {
        this.complaints = complaints;
    }
}
