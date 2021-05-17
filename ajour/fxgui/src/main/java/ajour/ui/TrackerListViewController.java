package ajour.ui;

import ajour.core.Tracker;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller for the tracker list view section in the FX app.
 */

public class TrackerListViewController {

    @FXML
    Text errorDialogue;
    @FXML
    Button addTrackerButton;
    @FXML
    Button removeTrackerButton;
    @FXML
    TextField addTrackerName;
    @FXML
    ListView<String> trackerListView;
    @FXML
    TrackerOverviewController overviewController;
    @FXML
    TrackerGraphController graphController;

    private ObservableList<String> observableContainer;
    private TrackerContainerAccess trackerContainerAccess;


    @FXML
    void initialize() {

        /* Sets up callback for the childControllers for saving/printing error dialogue */
        overviewController.setTrackerCallback(tracker -> {
            trackerContainerAccess.trackerChanged(tracker);
            graphController.plotLine();
            return null;
        });
        overviewController.setErrorDialogueCallback(string -> {
            errorDialogue.setText(string);
            return null;
        });
    }

    /**
     * Fetches the currently selected Tracker by checking the TrackerContainerAccess for the name
     * given by the String selected in the ListView, and returning it if it exists.
     *
     * @return the Tracker with the name selected in the ListView, otherwise null.
     */
    Tracker getSelectedTracker() {
        return trackerContainerAccess.getTracker(trackerListView.getSelectionModel().getSelectedItem());
    }

    /**
     * Selects the first tracker in the ListView if the underlying list is not empty.
     */
    private void selectFirstTracker() {
        if (!observableContainer.isEmpty()) {
            trackerListView.getSelectionModel().selectFirst();
        }
    }

    /**
     * Initializes the TrackerContainerAccess field and the underlying list for the ListView in the UI.
     * After initializing, it will also update and update the rest of the UI that depends on the access.
     *
     * @param trackerContainerAccess the TrackerContainerAccess to be used for the session.
     */
    protected void setTrackerDataAccess(TrackerContainerAccess trackerContainerAccess) {
        this.trackerContainerAccess = trackerContainerAccess;

        /* Initializes the underlying list for the ListView and gets the trackernames from
         the provided TrackerDataAccess object */
        observableContainer = FXCollections.observableList(new ArrayList<>());
        trackerListView.setItems(observableContainer);
        updateTrackerListView();
        setupListViewListener();
        selectFirstTracker();
        overviewController.updateOverview();
        graphController.plotLine();
    }

    /**
     * Adds a Tracker with the name written in the addTrackerName text field in the UI.
     * Checks for empty strings and if there is already a Tracker with same name already and
     * gives the user appropriate feedback.
     * After adding, the ListView will be updated, and the new Tracker selected.
     */
    @FXML
    private void addTracker() {
        String trackerName = addTrackerName.getText();
        if (!trackerName.equals("")) {
            if (trackerContainerAccess.hasTracker(trackerName)) {
                errorDialogue.setText("Tracker must have a unique name");
            } else {
                Tracker newTracker = new Tracker(trackerName);
                trackerContainerAccess.addTracker(newTracker);
                updateTrackerListView();
                trackerListView.getSelectionModel().select(trackerName);
                addTrackerName.clear();
                errorDialogue.setText("");
            }
        } else {
            errorDialogue.setText("Please enter a name for the Tracker");
        }
    }

    /**
     * Removes the selected Tracker, updates the ListView and selects the Tracker above the now removed
     * Tracker.
     */
    @FXML
    void removeSelectedTracker() {
        int removeIndex = trackerListView.getSelectionModel().getSelectedIndex();
        Tracker tracker = getSelectedTracker();
        if (tracker != null) {
            trackerContainerAccess.removeTracker(tracker.getName());
        }
        updateTrackerListView();
        selectOnDeletion(removeIndex);
    }

    /**
     * Fetches the names of the Tracker from the TrackerContainerAccess and updates the ListView.
     */
    void updateTrackerListView() {
        List<String> newList = new ArrayList<>(trackerContainerAccess.getTrackerNames());
        observableContainer.clear();
        observableContainer.addAll(newList);
    }

    /**
     * Changes selection after deletion in a way that prevents the selection from going
     * out of bounds. Will select the element one index below the provided index
     */
    private void selectOnDeletion(int removedIndex) {
        trackerListView.getSelectionModel().select(removedIndex - 1);
    }


    /**
     * Sets up the listener for the listview in the UI, that notifies the overview/graph-controllers,
     * so that the UI is updated to reflect the new selection.
     */

    private final InvalidationListener listViewListener = c -> {
        Tracker tracker = getSelectedTracker();
        overviewController.setSelectedTracker(tracker);
        graphController.setSelectedTracker(tracker);
    };

    private void setupListViewListener() {
        trackerListView.getSelectionModel()
            .selectedItemProperty()
            .removeListener(listViewListener);
        trackerListView.getSelectionModel()
            .selectedItemProperty()
            .addListener(listViewListener);
    }
}
