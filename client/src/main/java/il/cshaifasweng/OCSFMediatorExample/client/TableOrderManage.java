package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.List;

public class TableOrderManage {
    private static List<Integer> availableTablesIds;
    private static int branchId;
    private static String date;
    private static String time;
    private static int numberOfGuests;
    private static String location;

    // setters

    public static void setAvailableTablesIds(List<Integer> availableTablesIds) {
        TableOrderManage.availableTablesIds = availableTablesIds;
    }

    public static void setBranchId(int branchId) {
        TableOrderManage.branchId = branchId;
    }

    public static void setDate(String date) {
        TableOrderManage.date = date;
    }

    public static void setTime(String time) {
        TableOrderManage.time = time;
    }

    public static void setNumberOfGuests(int numberOfGuests) {
        TableOrderManage.numberOfGuests = numberOfGuests;
    }

    public static void setLocation(String location) {
        TableOrderManage.location = location;
    }


    // getters

    public static List<Integer> getAvailableTablesIds() {
        return availableTablesIds;
    }

    public static int getBranchId() {
        return branchId;
    }

    public static String getDate() {
        return date;
    }

    public static String getTime() {
        return time;
    }

    public static int getNumberOfGuests() {
        return numberOfGuests;
    }

    public static String getLocation() {
        return location;
    }

}
