package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Convertor {

    public static List<DishEnt> convertToDishEntList(List<Dish> dishes) {
        return dishes.stream()
                .map(dish -> {
                    // Pull out the List<String> first
                    List<String> ingredients = dish.getIngredients();
                    // Convert it to String[]
                    String[] ingredientsArr = ingredients.toArray(new String[ingredients.size()]);

                    // Now create the DishEnt
                    return new DishEnt(
                            dish.getId(),
                            dish.getName(),
                            dish.getDescription(),
                            ingredientsArr,
                            dish.getPrice(),
                            dish.getBranchID(),
                            dish.getImage(),
                            dish.isSalePrice(),
                            dish.getSalePrice()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert TableSchema -> TableEnt
     */
    public static TableEnt convertTableToEnt(TableSchema table) {
        if (table == null) return null;
        return new TableEnt(
                table.getTableId(),
                (table.getBranch() != null ? table.getBranch().getId() : -1), // or keep branchId
                table.getNumberOfDiners(),
                (table.getLocation() != null ? table.getLocation().name() : null)
        );
    }

    public static List<TableEnt> convertToTableEntList(List<TableSchema> tables) {
        if (tables == null) return null;
        return tables.stream()
                .map(Convertor::convertTableToEnt)
                .collect(Collectors.toList());
    }

    /**
     * Convert RestaurantBranch -> BranchEnt
     */
    public static BranchEnt convertToBranchEnt(RestaurantBranch branch) {
        if (branch == null) return null;
//        System.out.println("Converting RestaurantBranch: " + branch.getBranchName());

        // Convert tables
        List<TableEnt> tableEnts = convertToTableEntList(branch.getTables());

        // Create a new list for openingHours to avoid lazy initialization issues
        List<String> openingHours = (branch.getOpeningHours() != null) ?
                new ArrayList<>(branch.getOpeningHours()) : new ArrayList<>();

        // Convert it to String[]
        String[] openingHoursArr = openingHours.toArray(new String[openingHours.size()]);
        return new BranchEnt(
                branch.getId(),
                branch.getBranchName(),
                branch.getLocation(),
                openingHoursArr,
                tableEnts
        );
    }


    public static List<BranchEnt> convertToBranchEntList(List<RestaurantBranch> branches) {
        if (branches == null) return null;
        return branches.stream()
                .map(Convertor::convertToBranchEnt)
                .collect(Collectors.toList());
    }

    public static List<MenuChangeEnt> convertToMenuChangesEntList(List<MenuChanges> menuChanges) {
        return menuChanges.stream()
                .map(menuChange -> {

                    // Now create the DishEnt
                    return new MenuChangeEnt(
                            menuChange.getId(),
                            menuChange.getDishId(),
                            menuChange.getOldPrice(),
                            menuChange.isOldIsOnSale(),
                            menuChange.getOldSalePrice(),
                            menuChange.getNewPrice(),
                            menuChange.isNewIsOnSale(),
                            menuChange.getNewSalePrice()
                    );
                })
                .collect(Collectors.toList());
    }
    /**
     * Convert list of Complaints -> list of ComplaintEnt
     */
    public static ComplaintEnt convertToComplaintEnt(Complaint complaint) {
        if (complaint == null) return null;
        System.out.println("Converting Complaint: " + complaint.getComplaintId());
        ComplaintEnt convertedComplaint = new ComplaintEnt(complaint.getComplaintId(), complaint.getComplaint(), complaint.getDate(), complaint.getName(), complaint.getCreditCardNumber());
        return convertedComplaint;

    }
    public static List<ComplaintEnt> convertToComplaintEntList(List<Complaint> complaintsList) {
        if (complaintsList == null) return null;
        return complaintsList.stream()
                .map(Convertor::convertToComplaintEnt)
                .collect(Collectors.toList());
    }

}