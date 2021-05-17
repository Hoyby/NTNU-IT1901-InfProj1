package ajour.ui;

import ajour.core.Tracker;
import ajour.core.TrackerListener;
import ajour.core.utility.EntrySummer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Controller for the tracker overview section in the FX app.
 */

public class TrackerOverviewController {
    @FXML Text todayValue;
    @FXML Text yesterdayValue;
    @FXML Text weekValue;
    @FXML Text trackerName;
    @FXML TextField addEntryInput;
    @FXML
    Button addEntryButton;
    @FXML
    Button undoEntryButton;

    private Tracker selectedTracker;
    private Callback<Tracker, Void> trackerCallback;
    private Callback<String, Void> errorDialogueCallback;

    @FXML
    void initialize() {
        undoEntryButton.setDisable(true);
    }

    /**
     * Adding/removing listeners, on selection change, as well as keeping track of the current
     * selection by updating the selectedTracker field. This is in order to keep a reference to the
     * Tracker in case it is removed, as it still needs to be referenced for removing the listener and
     * other operations.
     */
    void setSelectedTracker(Tracker tracker) {
        if (this.selectedTracker != null) {
            selectedTracker.removeListener(trackerListener);
        }
        this.selectedTracker = tracker;
        updateOverview();
        if (this.selectedTracker != null) {
            selectedTracker.addListener(trackerListener);
        }
        undoEntryButton.setDisable(true);
    }

    /**
     * Adds entry to current tracker, or notifies the parent controller to print an explanation
     * in case of invalid input.
     */
    @FXML
    private void addEntry() {
        try {
            int fromInput = Integer.parseInt(addEntryInput.getText());
            selectedTracker.addEntry(fromInput);
            addEntryInput.clear();
            errorDialogueCallback.call("");
            undoEntryButton.setDisable(false);
        } catch (IllegalArgumentException e) {
            errorDialogueCallback.call("Please enter a number");
        } catch (Exception e) {
            errorDialogueCallback.call("An error occurred");
        }
    }

    /**
     * Removes the last added TrackerEntry from the selected tracker.
     */
    @FXML
    private void undoEntry() {
        try {
            selectedTracker.undoEntry();
            undoEntryButton.setDisable(true);
        } catch (IllegalStateException e) {
            errorDialogueCallback.call("No entries to undo");
        }
    }


    /**
     * Updates the view, while having a fallback in case the selected tracker has become null for any reason.
     */
    void updateOverview() {
        if (selectedTracker != null) {
            Tracker tracker = selectedTracker;
            trackerName.setText(tracker.getName());

            todayValue.setText(Integer.toString(tracker.sumEntries(EntrySummer.today)));
            yesterdayValue.setText(Integer.toString(tracker.sumEntries(EntrySummer.yesterday)));
            weekValue.setText(Integer.toString(tracker.sumEntries(EntrySummer.lastWeek)));

        } else {
            trackerName.setText("No tracker selected");
            todayValue.setText("");
            yesterdayValue.setText("");
            weekValue.setText("");
        }
    }

    /*
     * Methods for the parent controller, so that the UI can be updated or
     * notifying that a tracker object has been changed.
     */
    public void setTrackerCallback(Callback<Tracker, Void> trackerCallback) {
        this.trackerCallback = trackerCallback;
    }

    public void setErrorDialogueCallback(Callback<String, Void> errorDialogueCallback) {
        this.errorDialogueCallback = errorDialogueCallback;
    }

    /**
     * Listener that is attached/detached on selection, which notifies parent controller so that the dataaccess
     * is notified in order to update the UI and save/send the new data.
     */
    private final TrackerListener trackerListener = tracker -> {
        if (trackerCallback != null) {
            //trackerDataAccess.trackerChanged(tracker);
            trackerCallback.call(tracker);
        }
        updateOverview();
    };
}
