package ajour.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrackerTest {

    private Tracker testTracker;
    private int entry1;
    private int entry2;

    @BeforeEach
    public void setUp() {
        testTracker = new Tracker("Test_Tracker");
        entry1 = 10;
        entry2 = 11;

        testTracker.addEntry(entry1);
        testTracker.addEntry(entry2);
    }

    @Test
    public void testTrackerCollection() {
        final Iterator<TrackerEntry> it1 = testTracker.getEntries().iterator();

        TrackerEntry currentEntry = it1.next();
        assertEquals(entry1, currentEntry.getValue());

        currentEntry = it1.next();
        assertEquals(entry2, currentEntry.getValue());
    }

    @Test
    public void testTrackerConstructor_blank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tracker("");
        });
        assertEquals("Name cannot be blank", exception.getMessage());
    }

    @Test
    public void testTrackerConstructor_null() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tracker(null);
        });
        assertEquals("Argument cannot be null", exception.getMessage());
    }

    @Test
    public void testTrackerMethods() {
        final Tracker emptyTestTracker = new Tracker("emptyTest_Tracker");

        assertEquals("Test_Tracker", testTracker.getName());

        assertTrue(emptyTestTracker.isEmpty(), "The Tracker should be empty.");
        assertFalse(testTracker.isEmpty(), "The Tracker should not be empty");
    }

    @Test
    public void testAddEntry_negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            testTracker.addEntry(-1);
        });
        assertEquals("Entry value cannot be negative", exception.getMessage());
    }

    @Test
    public void testAddEntry_nullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            testTracker.addEntry(1, null);
        });
        assertEquals("The provided timestamp cannot be null.", exception.getMessage());
    }

    @Test
    public void testUndoEntry_removeFromList() {
        int initialSize = testTracker.getEntries().size();
        int initialSum = testTracker.sumEntries(entry -> true);

        testTracker.undoEntry();

        int newSize = testTracker.getEntries().size();
        int newSum = testTracker.sumEntries(entry -> true);

        assertTrue(initialSize > newSize);
        assertTrue(initialSum > newSum);
        assertEquals(entry1, testTracker.sumEntries(trackerEntry -> true));
        testTracker.undoEntry();
        assertTrue(testTracker.isEmpty());
    }

    @Test
    public void testUndoEntry_removeFromEmptyList() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            new Tracker("TestEmptyUndo").undoEntry();
        });
        assertEquals("Cannot undo entry on an empty Tracker", exception.getMessage());
    }

    @Test
    public void testTrackerListener() {
        AtomicBoolean firedEvent = new AtomicBoolean(false);
        TrackerListener listener = c -> {
            firedEvent.set(true);
        };
        Tracker tracker = new Tracker("Test");
        tracker.addListener(listener);
        tracker.addEntry(1337);
        assertTrue(firedEvent.get());

        firedEvent.set(false);
        tracker.removeListener(listener);
        tracker.addEntry(9001);
        assertFalse(firedEvent.get());
    }
}
