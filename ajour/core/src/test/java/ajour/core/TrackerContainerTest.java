package ajour.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TrackerContainerTest {

    Tracker tracker1;
    Tracker tracker2;
    Tracker tracker3;
    TrackerContainer trackerContainer1;
    List<Tracker> trackers;

    @BeforeEach
    public void setUp() {
        trackerContainer1 = new TrackerContainer();
        tracker1 = new Tracker("t1");
        tracker2 = new Tracker("t2");
        tracker3 = new Tracker("t2");

    }

    @Test
    public void testTrackerContainer() {
        trackers = new ArrayList<>(Arrays.asList(tracker1, tracker2, tracker3));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TrackerContainer(trackers);
        });
        assertEquals("Trackers must have unique names", exception.getMessage());
    }

    @Test
    public void testAddTracker() {
        trackers = new ArrayList<>(Arrays.asList(tracker1, tracker2));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TrackerContainer(trackers).addTracker(new Tracker("t1"));
        });
        assertEquals("Tracker must have a unique name", exception.getMessage());

    }

    @Test
    public void testContainsTracker() {
        trackerContainer1.addTracker(tracker1);
        Assertions.assertTrue(
            trackerContainer1.containsTracker(tracker1.getName()));
        Assertions.assertFalse(
            trackerContainer1.containsTracker(tracker2.getName()));
    }

    @Test
    public void testRemoveTracker() {
        trackerContainer1.addTracker(tracker1);
        if (trackerContainer1.containsTracker(tracker1.getName())) {
            trackerContainer1.removeTracker(tracker1.getName());
            Assertions.assertFalse(trackerContainer1.containsTracker(tracker1.getName()));
        } else {
            Assertions.fail("The added Tracker could not be found before testing removal");
        }
    }

    @Test
    public void testGetTracker() {
        trackerContainer1.addTracker(tracker1);
        Assertions.assertEquals(tracker1, trackerContainer1.getTracker(tracker1.getName()));
        Assertions.assertNull(trackerContainer1.getTracker(tracker2.getName()));
    }

}
