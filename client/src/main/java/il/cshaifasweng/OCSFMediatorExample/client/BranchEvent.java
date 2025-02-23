package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchEnt;

import java.util.List;

public class BranchEvent {
    private List<BranchEnt> BranchList;

    public BranchEvent(List<BranchEnt> BranchList) {
        this.BranchList = BranchList;
    }

    public List<BranchEnt> getBranchList() {
        return BranchList;
    }
}


