package ajour.core;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * This class stores an int value and a timestamp to be used/contained within a {@link Tracker}.
 * An {@link Instant} from the java.time library introduced in Java 8 is used as the timestamp.
 */
public class TrackerEntry {

    private final int value;
    private final Instant timestamp;


    /**
     * Initializes a TrackerEntry with a value and the time of initialization.
     *
     * @param value the value in the form of an integer to be stored in this object.
     */
    public TrackerEntry(int value) {
        this(value, Instant.now());
    }

    /**
     * Alternative constructor for using a provided timestamp.
     *
     * @param timestamp an {@link Instant} to used as a custom timestamp.
     * @throws IllegalArgumentException if the provided value is negative, or the timestamp is null.
     */
    public TrackerEntry(int value, Instant timestamp) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("The provided timestamp cannot be null.");
        }
        this.value = value;
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    /**
     * Copies the timestamp to a new instance of {@link Instant} and returns it.
     *
     * @return a copy of the timestamp as an {@link Instant}
     */
    public Instant getTimeStamp() {
        return Instant.from(this.timestamp);
    }

    @Override
    public String toString() {
        return value + " - " + this.timestamp.truncatedTo(ChronoUnit.SECONDS);
    }

}
