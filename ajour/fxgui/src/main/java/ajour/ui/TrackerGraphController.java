package ajour.ui;

import ajour.core.Tracker;
import ajour.core.TrackerEntry;
import ajour.core.utility.EntrySummer;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class TrackerGraphController {

    @FXML
    Pane trackerGraph;
    @FXML
    Button selectToday;
    @FXML
    Button selectLastSeven;
    @FXML
    Button selectLastThirty;
    @FXML
    Pane graphPane;

    private HashMap<Integer, Integer> map;
    private Tracker selectedTracker;
    private final String timezone = TimeZone.getDefault().getID();

    @FXML
    private void initialize() {

    }

    /**
     * Initializes a LineChart with axis drawn depending on button selected.
     * Defaults to today, when triggered by selection change in the UI.
     *
     * @return the corresponding LineChart
     */
    private LineChart<Number, Number> drawGraph() {
        NumberAxis xnumAxis;
        NumberAxis ynumAxis;
        LineChart<Number, Number> chart;

        if (selectLastSeven.isFocused()) {
            xnumAxis = new NumberAxis(-7, 0, 1);
            ynumAxis = new NumberAxis();
            xnumAxis.setLabel("Days since entry");
            ynumAxis.setLabel("Value of entries");
            chart = new LineChart<>(xnumAxis, ynumAxis);

        } else if (selectLastThirty.isFocused()) {
            xnumAxis = new NumberAxis(-30, 0, 2);
            ynumAxis = new NumberAxis();
            xnumAxis.setLabel("Days since entry");
            ynumAxis.setLabel("Value of entries");
            chart = new LineChart<>(xnumAxis, ynumAxis);

        } else {
            xnumAxis = new NumberAxis(0, 24, 2);
            ynumAxis = new NumberAxis();
            xnumAxis.setLabel("Time of entry");
            ynumAxis.setLabel("Value of entries");
            chart = new LineChart<>(xnumAxis, ynumAxis);
        }
        chart.setMaxWidth(800);
        chart.setMaxHeight(225);
        return chart;
    }

    /**
     * Creates a HashMap of entries in a specific timeframe, and their values.
     *
     * @param entries    takes a list of {@link TrackerEntry}s
     * @param timeSelect select the time to return 1=hour, 2=week, 3=month
     * @return a HashMap with {key=time, value=value of trackers in time}
     */
    private HashMap<Integer, Integer> createMap(List<TrackerEntry> entries, int timeSelect) {
        map = new HashMap<>();
        Instant now = Instant.now();
        int currentDayOfYear = now.atZone(ZoneId.of(timezone)).getDayOfYear();

        for (TrackerEntry entry : entries) {
            int time = switch (timeSelect) {
                case 1 -> entry.getTimeStamp().atZone(ZoneId.of(timezone)).getHour();
                case 2, 3 -> -(currentDayOfYear - entry.getTimeStamp().atZone(ZoneId.of(timezone)).getDayOfYear());
                default -> -1;
            };

            if (map.containsKey(time)) {
                map.put(time, map.get(time) + entry.getValue());
            } else {
                map.put(time, entry.getValue());
            }
        }
        return map;
    }

    /**
     * Creates a HashMap of entries depending on the the button pressed by calling the createMap function.
     *
     * @return a HashMap with {key=time, value=value of trackers in time}
     */
    private HashMap<Integer, Integer> getData() {
        map = null;

        //Returns a list of trackers created in the time given by the predicate
        List<TrackerEntry> todayEntries = EntrySummer.getEntriesFromPred(selectedTracker, EntrySummer.today);
        List<TrackerEntry> weekEntries = EntrySummer.getEntriesFromPred(selectedTracker, EntrySummer.lastWeek);
        List<TrackerEntry> monthEntries = EntrySummer.getEntriesFromPred(selectedTracker, EntrySummer.lastMonth);


        if (selectLastSeven.isFocused()) {
            map = createMap(weekEntries, 2);
        } else if (selectLastThirty.isFocused()) {
            map = createMap(monthEntries, 3);
        } else {
            map = createMap(todayEntries, 1);
        }
        return map;
    }

    /**
     * Creates a Series from the data returned by getData.
     *
     * @return Series of relevant data depending on the button status.
     */
    private XYChart.Series<Number, Number> createSeries() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        if (selectedTracker != null && !selectedTracker.isEmpty()) {
            map = getData();
            if (map != null) {
                map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
            }
        }
        return series;
    }

    /**
     * Plots the lines in the graph and adds it on to the GUI.
     */
    public void plotLine() {
        graphPane.getChildren().clear();
        LineChart<Number, Number> graph = drawGraph();
        graph.setLegendVisible(false);

        XYChart.Series<Number, Number> series = createSeries();

        graph.getData().add(series);
        graph.setMaxWidth(800);
        graph.setMaxHeight(250);

        graphPane.getChildren().add(graph);
    }

    /**
     * Used by the parent controller to inform this controller which {@link Tracker} to use.
     * @param selectedTracker the selected {@link Tracker}
     */
    public void setSelectedTracker(Tracker selectedTracker) {
        this.selectedTracker = selectedTracker;
        plotLine();
    }
}

