package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.Embeddable;

@Embeddable
public class BuyerDetails {

    private String creditCard;
    private String name;
    private String address;
    private String phone;

    // Constructors
    public BuyerDetails() {
    }

    public BuyerDetails(String creditCard, String name, String address, String phone) {
        this.creditCard = creditCard;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Getters and setters
    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}