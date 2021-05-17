package ajour.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrackerEntryTest {

    private TrackerEntry entry1;
    private TrackerEntry entry2;

    @BeforeEach
    public void setUp() {
        Instant instant = Instant.now();

        entry1 = new TrackerEntry(10);
        entry2 = new TrackerEntry(15, instant);
    }

    @Test
    public void testConstructor_nullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TrackerEntry(20, null);
        });
        assertEquals("The provided timestamp cannot be null.", exception.getMessage());
    }

    @Test
    public void testConstructor_negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TrackerEntry(-1);
        });
        assertEquals("Value cannot be negative", exception.getMessage());
    }

    @Test
    public void testTrackerEntryValues() {
        final Instant instant = Instant.now();

        /*Tests entry properties and methods*/
        assertEquals(10, entry1.getValue(), "Value is " + entry1.getValue() + " - Should to be " + 10.0);
        assertTrue(
            (instant.toEpochMilli() - entry1.getTimeStamp().toEpochMilli()) < 1000
            , "Difference between dates is too big: " + (instant.toEpochMilli() - entry1.getTimeStamp().toEpochMilli()));

        assertEquals("10 - " + entry1.getTimeStamp().truncatedTo(ChronoUnit.SECONDS), entry1.toString());
        assertEquals(15, entry2.getValue(), "Value is " + entry1.getValue() + " - Should to be " + 15.5);

        assertTrue(
            (instant.toEpochMilli() - entry2.getTimeStamp().toEpochMilli()) < 1000
            , "Difference between dates is too big: " + (instant.toEpochMilli() - entry2.getTimeStamp().toEpochMilli()));
        assertEquals("15 - " + instant.truncatedTo(ChronoUnit.SECONDS), entry2.toString());
    }
}
