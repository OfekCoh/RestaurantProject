package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchReportEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.BranchReportEnt;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class BranchReportController {

    @FXML
    private BarChart<String, Number> orderChart;

    @FXML
    private Label headlineText;

    @FXML
    private BarChart<String, Number> peopleChart;

    @FXML
    private BarChart<String, Number> complaintsChart;

    @FXML
    private Label orderSummaryLabel;

    @FXML
    private Label peopleSummaryLabel;

    @FXML
    private Label complaintsSummaryLabel;

    // Get the branch report from the reportEnt (from db server) and generate hist to screen
    public void loadBranchReport(BranchReportEnt report) {
        orderChart.getData().clear();
        peopleChart.getData().clear();
        complaintsChart.getData().clear();

        // Generate histograms
        XYChart.Series<String, Number> ordersSeries = prepareHistogramData(report.getOrdersPerDay(), "Orders per Day");
        XYChart.Series<String, Number> peopleSeries = prepareHistogramData(report.getPeoplePerDay(), "People per Day");
        XYChart.Series<String, Number> complaintsSeries = prepareHistogramData(report.getComplaintsPerDay(), "Complaints per Day");

        // Add data to charts
        orderChart.getData().add(ordersSeries);
        peopleChart.getData().add(peopleSeries);
        complaintsChart.getData().add(complaintsSeries);

        // Set summary labels by summing the list values using sum()
        int totalOrders = report.getOrdersPerDay().stream().mapToInt(Integer::intValue).sum();
        int totalPeople = report.getPeoplePerDay().stream().mapToInt(Integer::intValue).sum();
        int totalComplaints = report.getComplaintsPerDay().stream().mapToInt(Integer::intValue).sum();

        orderSummaryLabel.setText("Total Orders: " + totalOrders + " | Failed Orders: " + report.getFailedOrders() + " | Total income: " + report.getTotalOrdersIncome());
        peopleSummaryLabel.setText("Total People: " + totalPeople);
        complaintsSummaryLabel.setText("Total Complaints: " + totalComplaints + " | Complaints handled automatically: " + report.getComplaintsHandledAutomatically());
    }

    // Prepare hist from integer list
    private XYChart.Series<String, Number> prepareHistogramData(List<Integer> dataList, String seriesName) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        for (int i = 0; i < dataList.size(); i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), dataList.get(i)));
        }

        return series;
    }

    // When recieve branch report from server using event bus
    @Subscribe
    public void onCompliantEvent(BranchReportEvent event) {
        Platform.runLater(() -> {
            System.out.println("Received BranchReportEvent");
            loadBranchReport(event.getBranchReport());

        });
    }

    @FXML
    public void initialize() {
        // Recieve the branch report from the server (through client and event bus)
        EventBus.getDefault().register(this);
        headlineText.setText("Branch Report For Branch: " + ChooseReportController.getSelectedBranchName() + " For Month: "+ ChooseReportController.getSelectedMonth() + "/" + ChooseReportController.getSelectedYear());
        try {
            SimpleClient.getClient().sendGetBranchReport(ChooseReportController.getSelectedYear(), ChooseReportController.getSelectedMonth(), ChooseReportController.getSelectedBranchId());
            System.out.println("requested branch report from server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void goBack(ActionEvent event) throws Exception {
        App.setRoot("chooseReport");
    }
}
