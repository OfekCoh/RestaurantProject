package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private int selectedBranch;
    private boolean isDelivery;

    @ElementCollection
    @CollectionTable(name = "order_dishes", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "dish_id")
    private List<Integer> dishes;

    @ElementCollection
    @CollectionTable(name = "order_adaptations", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "adaptation")
    private List<String> adaptation;

    // Embed BuyerDetails into the same table (orders)
    @Embedded
    private BuyerDetails buyerDetails;

    /**
     * Status is stored as an integer in the database:
     * 0 = Pending
     * 1 = Full Refund - Canceled
     * 2 = Partly Refund
     * 3 = No refund
     * 4 = Completed
     */
    private Date orderDate;
    private double finalPrice;
    private int status;


    public Order() {
    }

    public Order(int selectedBranch, boolean isDelivery, List<Integer> dishes,
                 List<String> adaptation, BuyerDetails buyerDetails, Date orderDate, double finalPrice) {
        this.selectedBranch = selectedBranch;
        this.isDelivery = isDelivery;
        this.dishes = dishes;
        this.adaptation = adaptation;
        this.buyerDetails = buyerDetails;
        this.orderDate = orderDate;
        this.finalPrice = finalPrice;
        this.status = 0; // default to "pending"
    }

    // Getters and Setters

    public int getOrderId() {
        return orderId;
    }

    public int getSelectedBranch() {
        return selectedBranch;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public List<Integer> getDishes() {
        return dishes;
    }

    public List<String> getAdaptation() {
        return adaptation;
    }

    public BuyerDetails getBuyerDetails() {
        return buyerDetails;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setSelectedBranch(int selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public void setDishes(List<Integer> dishes) {
        this.dishes = dishes;
    }

    public void setAdaptation(List<String> adaptation) {
        this.adaptation = adaptation;
    }

    public void setBuyerDetails(BuyerDetails buyerDetails) {
        this.buyerDetails = buyerDetails;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Converts the integer 'status' to a human-readable string.
     * You can call this whenever you want a string representation.
     */
    public String getStatusDescription() {
        switch (status) {
            case 0:
                return "pending";
            case 1:
                return "Full Refund";
            case 2:
                return "Partly Refund";
            case 3:
                return "No refund";
            case 4:
                return "completed";
            default:
                return "Unknown status (" + status + ")";
        }
    }
}