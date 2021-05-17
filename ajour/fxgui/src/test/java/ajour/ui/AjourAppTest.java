package ajour.ui;


import static javafx.application.Platform.runLater;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import ajour.core.TrackerContainer;
import ajour.json.TrackerJson;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Isolated fxgui test that does not use the RemoteTrackerContainerAccess class, which is tested in the
 * integration test module instead.
 * A note about Platform.runLater(): we have found that using this runner for accessing the controllers for
 * relevant assertions provides the most consistency for testing on different PC's and laptops as well as
 * gitpod when used in conjunction with waitForFxEvents().
 */

public class AjourAppTest extends ApplicationTest {


    private AjourAppController controller;

    // Creating a TrackerJson-object to read trackers that has been added/removed from the ui.
    private TrackerJson trackerJson;
    // Need the config-data from ajour.properties in order to get the correct path for the json-file.
    private Configuration config;

    String trackerName;
    String trackerToBeRemoved;

    //Test: legge til tracker, fjerne tracker, Legge til entry, fjerne entry(ikke enda),
    //Sjekke at verdiene stemmer (blir oppdatert, ui vs objekt)

    @Override
    public void start(final Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("AjourApp.fxml"));
        final Parent root = loader.load();
        this.controller = loader.getController();
        trackerJson = new TrackerJson();
        config = new Configuration();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setup() {
        trackerName = "TestTracker";
        trackerToBeRemoved = "Remove this tracker";
        waitForFxEvents();
    }

    @AfterEach
    void tearDown() {
        Path.of(System.getProperty("user.home"), config.getProperty("savePath")).toFile().delete();
    }

    @Test
    public void testAddTracker_blankInput() {

        addTracker(trackerName);
        assertTrue(Objects.requireNonNull(getCurrentTrackerContainer()).containsTracker(trackerName));
        addTracker("");
        assertEquals("Please enter a name for the Tracker", controller.listViewController.errorDialogue.getText());
    }

    @Test
    public void testAddTracker_duplicateInput() {
        addTracker(trackerName);
        addTracker(trackerName);
        assertEquals("Tracker must have a unique name", controller.listViewController.errorDialogue.getText());
    }

    @Test
    public void testRemoveTracker() {
        addTracker(trackerToBeRemoved);
        removeTracker(trackerToBeRemoved);
        assertFalse(Objects.requireNonNull(getCurrentTrackerContainer()).containsTracker(trackerToBeRemoved)
            , "The tracker was not removed");
    }

    @Test
    public void testAddEntry() {
        int entryValue = 5;
        final Instant timestampCheck = Instant.now();
        addTracker(trackerName);
        addEntry(trackerName, entryValue);
        assertFalse(Objects.requireNonNull(getCurrentTrackerContainer()).getTracker(trackerName).isEmpty()
            , "Tracker is empty after adding entry");
        assertTrue(getCurrentTrackerContainer()
                .getTracker(trackerName)
                .getEntries()
                .stream()
                .anyMatch(entry -> entry.getValue() == entryValue && entry.getTimeStamp().isAfter(timestampCheck))
            , "Added entry does not match the parameters the used to create the entry");
    }

    /**
     * Undo-button should be disabled at start, enabled whenever an entry has been added, and disabled again
     * after undoing one entry. It should also be disabled when selecting or adding a new Tracker,
     * at which point it is too late to undo.
     */
    @Test
    public void testUndoEntry() {
        TrackerOverviewController overviewController = controller.listViewController.overviewController;
        runLater(() -> assertTrue(overviewController.undoEntryButton.isDisabled()));
        waitForFxEvents();

        addTracker(trackerName);
        runLater(() -> assertTrue(overviewController.undoEntryButton.isDisabled()));
        waitForFxEvents();

        addEntry(trackerName, 5);
        runLater(() -> assertFalse(overviewController.undoEntryButton.isDisabled()));
        waitForFxEvents();

        clickOn("#undoEntryButton");
        assertEquals("0", overviewController.todayValue.getText());
        runLater(() -> assertTrue(overviewController.undoEntryButton.isDisabled()));
    }

    /**
     * Implementing a test to see that the graph representation is correct would be as complex as implementing the graphs themselves. Having been tested
     * visually during implementation which, although isn't a guarantee for correct behaviour, we will test that it runs without throwing exceptions.
     */
    @Test
    void checkGraphButtons() {
        final String name1 = "graphtest1";
        final String name2 = "graphtest2";
        addTracker(name1);
        addTracker(name2);
        addEntry(name2, 10);

        selectTracker(name1);
        clickOn("#selectToday").clickOn("#selectLastSeven").clickOn("#selectLastThirty");

        selectTracker(name2);
        clickOn("#selectToday").clickOn("#selectLastSeven").clickOn("#selectLastThirty");

    }

    /*
     * After this test has run, the underlying TrackerContainer should contain the following Trackers:
     * Test1 with an entry of value 5, Test2 with an entry of value 12 and Test3 with an entry of value 20*/
    @Test
    public void testScenario() {

        final Instant startOfTest = Instant.now();
        final Text todayValue = controller.listViewController.overviewController.todayValue;

        /* Starts off by adding the trackers and the corresponding entries */
        final String test1 = "Test1";
        final String test2 = "Test2";
        final String test3 = "Test3";
        addTracker(test1);
        addEntry(test1, 5);
        addTracker(test2);
        addTracker(test3);
        addEntry(test2, 12);
        addEntry(test3, 20);

        /* Checking both the UI and using the TrackerJson class to read of the data saved to file to see that they
         * both match the trackers and entries added. */

        // Checking the TrackerContainer saved to file.
        TrackerContainer container = getCurrentTrackerContainer();
        if (container != null) {
            assertTrue(container.containsTracker(test1)
                && container.containsTracker(test1)
                && container.containsTracker(test1));
            assertEquals(5, container.getTracker(test1).sumEntries(entry -> entry.getTimeStamp().isAfter(startOfTest)));
            assertEquals(12, container.getTracker(test2).sumEntries(entry -> entry.getTimeStamp().isAfter(startOfTest)));
            assertEquals(20, container.getTracker(test3).sumEntries(entry -> entry.getTimeStamp().isAfter(startOfTest)));
        } else {
            fail("Could not get the underlying TrackerContainer");
        }


        // Checking that the correct values are written in the UI on selection.
        selectTracker(test1);
        assertEquals("5", todayValue.getText());
        selectTracker(test2);
        assertEquals("12", todayValue.getText());
        selectTracker(test3);
        assertEquals("20", todayValue.getText());
    }

    @Test
    public void testLoadFile() {
        runLater(() -> {
            try {
                controller.listViewController.setTrackerDataAccess(
                    new LocalTrackerContainerAccess(Path.of(getClass().getResource("testDefaultTrackers.json").toURI())));
            } catch (URISyntaxException e) {
                fail();
            }
        });
        waitForFxEvents();
        selectTracker("Workout");
        selectTracker("Sleep");
        Path.of(System.getProperty("user.home"), "testDefaultTrackers.Json").toFile().delete();
    }

    /* Utility methods for common operations, such as adding trackers, adding entries etc. */

    private void addTracker(String name) {
        waitForFxEvents();
        clickOn("#addTrackerName")
            .write(name)
            .clickOn("#addTrackerButton");
    }

    private void addEntry(String tracker, int value) {
        waitForFxEvents();
        selectTracker(tracker);
        clickOn("#addEntryInput")
            .write(String.valueOf(value))
            .clickOn("#addEntryButton");
    }

    private void removeTracker(String tracker) {
        waitForFxEvents();
        selectTracker(tracker);
        clickOn("#removeTrackerButton");
    }

    private void selectTracker(String tracker) {
        runLater(() -> controller.listViewController.trackerListView.getSelectionModel().select(tracker));
        waitForFxEvents();
    }

    private TrackerContainer getCurrentTrackerContainer() {
        Path savePath = Path.of(System.getProperty("user.home"), config.getProperty("savePath"));
        try (FileReader reader = new FileReader(savePath.toFile())) {
            return trackerJson.loadObject(reader);
        } catch (IOException e) {
            fail("Could not get the current trackers from " + savePath.toString());
        }
        return null;
    }
}
