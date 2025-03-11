package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManage {
    private static List<Integer> dishIds = new ArrayList<>();
    private static List<String> adaptations = new ArrayList<>();

    private static String orderType; //Options: Pickup/Delivery
    private static int selectedBranch;//Selected restaurant branchId
    private static Date orderDate;

    private static double deliveryFixedCost = 15.50;
    private static double finalPrice;


    public static void setFinalPrice(double finalPrice) {
        OrderManage.finalPrice = finalPrice;
    }

    public static double getFinalPrice() {
        return finalPrice;
    }

    public static void setSelectedBranch(int selectedBranch) {
        OrderManage.selectedBranch = selectedBranch;
    }

    public static int getSelectedBranch() {
        return OrderManage.selectedBranch;
    }

    public static void setOrderType(String orderType) {
        OrderManage.orderType = orderType;
    }

    public static String getOrderType() {
        return orderType;
    }

    public static void setOrderDate(Date orderDate) {
        OrderManage.orderDate = orderDate;
    }

    public static Date getOrderDate() {
        return orderDate;
    }

    public static double getDeliveryFixedCost() {
        return deliveryFixedCost;
    }

    // Add N copies of a dish
    public static void addDishes(int dishId, int quantity, String adaptationString) {
        for (int i = 0; i < quantity; i++) {
            dishIds.add(dishId);
            adaptations.add(adaptationString);
        }
    }

    public static List<Integer> getDishIds() {
        return dishIds;
    }

    public static List<String> getAdaptations() {
        return adaptations;
    }

    public static void clearCart() {
        dishIds.clear();
        adaptations.clear();
    }

    // NEW: remove a single item at the given index if valid
    public static void removeItem(int index) {
        if (index >= 0 && index < dishIds.size()) {
            dishIds.remove(index);
            adaptations.remove(index);
        }
    }
}
