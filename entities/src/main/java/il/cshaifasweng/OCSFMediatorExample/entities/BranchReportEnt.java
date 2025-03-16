// contains needed knowledge from database to show report for branch
package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;
import java.util.List;

public class BranchReportEnt implements Serializable {
    private int branchId;
    private int year;
    private int month;
    private int failedOrders;
    private int complaintsHandledAutomatically;
    private double totalOrdersIncome;
    private double totalComplaintsRefund;
    private List<Integer> ordersPerDay;
    private List<Integer> peoplePerDay;
    private List<Integer> complaintsPerDay;

    public BranchReportEnt(int branchId, int year, int month, int failedOrders, double totalComplaintsRefund, double totalOrdersIncome, int complaintsHandledAutomatically,List<Integer> ordersPerDay, List<Integer> peoplePerDay, List<Integer> complaintsPerDay) {
        this.branchId = branchId;
        this.year = year;
        this.month = month;
        this.failedOrders = failedOrders;
        this.totalComplaintsRefund = totalComplaintsRefund;
        this.totalOrdersIncome = totalOrdersIncome;
        this.complaintsHandledAutomatically = complaintsHandledAutomatically;
        this.ordersPerDay = ordersPerDay;
        this.peoplePerDay = peoplePerDay;
        this.complaintsPerDay = complaintsPerDay;
    }

    // getters
    public int getComplaintsHandledAutomatically(){ return complaintsHandledAutomatically;}
    public double getTotalOrdersIncome(){ return totalOrdersIncome;}
    public double getTotalComplaintsRefund(){ return totalComplaintsRefund;}
    public int getBranchId() { return branchId; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getFailedOrders() { return failedOrders; }
    public List<Integer> getOrdersPerDay() { return ordersPerDay; }
    public List<Integer> getPeoplePerDay() { return peoplePerDay; }
    public List<Integer> getComplaintsPerDay() { return complaintsPerDay; }
}
