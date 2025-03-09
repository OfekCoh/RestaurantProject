package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.Embeddable;

@Embeddable
public class BuyerDetails {
    private String name;
    private String address;
    private String phone;
    private String userId;
    private String cardNumber;
    private int cardMonth;
    private int cardYear;
    private String cvv;

    // Constructor, getters, and setters
    public BuyerDetails() {
    }

    public BuyerDetails(String name, String address, String phone, String userId,
                        String cardNumber, int cardMonth, int cardYear, String cvv) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardMonth = cardMonth;
        this.cardYear = cardYear;
        this.cvv = cvv;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardMonth(int cardMonth) {
        this.cardMonth = cardMonth;
    }

    public int getCardMonth() {
        return cardMonth;
    }

    public void setCardYear(int cardYear) {
        this.cardYear = cardYear;
    }

    public int getCardYear() {
        return cardYear;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCvv() {
        return cvv;
    }
}