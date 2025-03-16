package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchReportEnt;

public class BranchReportEvent {
    private BranchReportEnt branchReport;
    public BranchReportEvent(BranchReportEnt branchReport) {
        this.branchReport = branchReport;
    }
    public BranchReportEnt getBranchReport() {
        return branchReport;
    }
}
