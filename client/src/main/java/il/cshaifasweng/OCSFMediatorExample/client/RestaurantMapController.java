package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.RestaurantMapEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.TableEnt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RestaurantMapController {

    private static int branchId = 0;// The restaurant branch
    public static void setBranchId(int branchId) {
        RestaurantMapController.branchId = branchId;
    }


    // Seperate grid for inside and outside
    @FXML
    private GridPane insideTableGrid;
    @FXML
    private GridPane outsideTableGrid;

    private static  final int ROW_SIZE = 4;// how many tables each row

    private int minTableId = 0; // will be used to assigin tables to array and grid based on id

    private TableEnt[] tablesArray; // holds table data

    private  boolean[] availabilityArray; // tracks availability status


    // Receive available and taken table lists from server using EventBus
    @Subscribe
    public void onRestaurantMapEvent(RestaurantMapEvent event) {
        System.out.println("RestaurantMapEvent "+ branchId);
        // Only queue the UI update if it's for the right branch
        if (event.getBranchId() != branchId) {
            System.out.println("Skipping event: expected branch " + branchId + ", but got " + event.getBranchId());
            return;
        }

        Platform.runLater(() -> {
            System.out.println("Received new restaurant map event");
            updateTables(event.getAvailableTables(), event.getTakenTables());// update grid according to tables

        });
    }

    @FXML
    public void initialize() {
        // register to event bus to receive complaints
        EventBus.getDefault().register(this);
        tablesArray = null;
        availabilityArray = null;
        try {
            SimpleClient.getClient().sendGetTablesForMap(RestaurantMapController.branchId);
            System.out.println("Sent request to get table for map");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTables(List<TableEnt> newFreeTables, List<TableEnt> newTakenTables) {
        Platform.runLater(() -> {
            // If this is the first update, initialize the arrays and fill the grid
            if (tablesArray == null) {
                initializeTableArrays(newFreeTables, newTakenTables);
                fillGrid();
                return;
            }

            // Check the tables in the received lists and update only changed tables if needed
            for (TableEnt table : newFreeTables) {
                if (!availabilityArray[table.getTableId() - minTableId]) { // If previously taken, now free
                    updateGrid(table, true);
                    System.out.println("updated table" + table.getTableId());
                    availabilityArray[table.getTableId() - minTableId] = true; // Mark as available
                }
            }
            for (TableEnt table : newTakenTables) {
                if (availabilityArray[table.getTableId() - minTableId]) { // If previously free, now taken
                    updateGrid(table, false);
                    System.out.println("updated table" + table.getTableId());
                    availabilityArray[table.getTableId() - minTableId] = false; // Mark as taken
                }
            }
        });
    }


    // This function is called only once when the first update arrives to initilize the arrays
    private void initializeTableArrays(List<TableEnt> freeTables, List<TableEnt> takenTables) {
        // number of tables in the branch
        int num_of_tables = freeTables.size() + takenTables.size();

        // get the lowest table id
        int first_available_table_id = Integer.MAX_VALUE;
        int first_taken_table_id = Integer.MAX_VALUE;
        if(!freeTables.isEmpty()){
            first_available_table_id = freeTables.getFirst().getTableId();
        }
        if(!takenTables.isEmpty()){
            first_taken_table_id = takenTables.getFirst().getTableId();
        }
        // to prevent out of bounds if ids not starting from 0
        minTableId = Math.min(first_available_table_id, first_taken_table_id);

        // create arrays
        tablesArray = new TableEnt[num_of_tables];
        availabilityArray = new boolean[num_of_tables];

        for (TableEnt table : freeTables) {
            tablesArray[table.getTableId() - minTableId] = table;
            availabilityArray[table.getTableId() - minTableId] = true; // Mark table as available
        }

        for (TableEnt table : takenTables) {
            tablesArray[table.getTableId() - minTableId] = table;
            availabilityArray[table.getTableId() - minTableId] = false; // Mark table as taken
        }
    }


    // Clears and fills the entire grid (only called once)
    private void fillGrid() {
        insideTableGrid.getChildren().clear();
        outsideTableGrid.getChildren().clear();

        int insideCol = 0, insideRow = 0;
        int outsideCol = 0, outsideRow = 0;

        for (int i = 0; i < tablesArray.length; i++) {

            VBox tableBox = createTableBox(tablesArray[i], availabilityArray[i]);// Create table graphics

            if (tablesArray[i].getLocation().equalsIgnoreCase("INDOOR")) {// Check if inside or outside to update relevant grid
                insideTableGrid.add(tableBox, insideCol, insideRow);
                insideCol++;
                if (insideCol > ROW_SIZE) { // Number of tables in a row
                    insideCol = 0;
                    insideRow++;
                }
            } else {
                outsideTableGrid.add(tableBox, outsideCol, outsideRow);
                outsideCol++;
                if (outsideCol > ROW_SIZE) { // Number of tables in a row
                    outsideCol = 0;
                    outsideRow++;
                }
            }
        }
    }

    // Updates only one table in the grid when its availability changes
    private void updateGrid(TableEnt table, boolean isAvailable) {
        VBox tableBox = createTableBox(table, isAvailable); // Create updated graphics

        // Check if table is indor or outdoor to know which gird to update
        boolean isIndoor = table.getLocation().equals("INDOOR");
        GridPane targetGrid;
        if (isIndoor) {
            targetGrid = insideTableGrid;
        }
        else{
            targetGrid = outsideTableGrid;
        }

        // Find the cell containing the changed table
        Node existingNode = null;
        for (Node node : targetGrid.getChildren()) {
            if (node.getId() != null && node.getId().equals(String.valueOf(table.getTableId()))) {
                existingNode = node;
                break;
            }
        }

        // Get the tables row and col in the grid
        int col = GridPane.getColumnIndex(existingNode);
        int row = GridPane.getRowIndex(existingNode);

        // Remove the existing node at the calculated position
        targetGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row);

        // Add the updated table at the exact position
        targetGrid.add(tableBox, col, row);
    }

    // Creates the table graphics (graphics is a big word for a sqare with a color and some text)
    private VBox createTableBox(TableEnt table, boolean isAvailable) {
        Rectangle tableShape = new Rectangle(50, 50);
        Label label = new Label(isAvailable ? String.valueOf(table.getNumberOfDiners()) : "TAKEN");

        // Set color - green with green text if available gray with black text if taken
        if (isAvailable) {
            tableShape.setFill(Color.LIGHTGREEN);
            label.setTextFill(Color.DARKGREEN);
        } else {
            tableShape.setFill(Color.DARKGRAY);
            label.setTextFill(Color.BLACK);
        }

        tableShape.setStroke(Color.BLACK);

        // Tooltip displays table details
        Tooltip tooltip = new Tooltip("Table ID: " + table.getTableId() +
                "\nDiners: " + table.getNumberOfDiners() +
                "\nLocation: " + table.getLocation());
        Tooltip.install(tableShape, tooltip);

        VBox tableBox = new VBox(tableShape, label);
        tableBox.setId(String.valueOf(table.getTableId()));
        tableBox.setSpacing(5);
        return tableBox;
    }



    @FXML
    void goBack(ActionEvent event) throws Exception {
        //SimpleClient.getClient().sendGetMenuCommand();
        BranchSelectionController.setNextWindow("restaurant map");
        App.setRoot("branchSelection");
        EventBus.getDefault().unregister(this);
    }
}
