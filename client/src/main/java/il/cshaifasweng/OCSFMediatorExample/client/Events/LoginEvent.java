package il.cshaifasweng.OCSFMediatorExample.client.Events;

public class LoginEvent {
    private final int userID;
    private final int ruleID;

    public LoginEvent(int userID, int ruleID) {
        this.userID = userID;
        this.ruleID = ruleID;
    }

    public int getUserID() {
        return userID;
    }

    public int getRuleID() {
        return ruleID;
    }
}