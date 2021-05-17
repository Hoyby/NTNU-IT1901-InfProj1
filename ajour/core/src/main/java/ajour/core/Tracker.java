package ajour.core;

import ajour.core.utility.EntrySummer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class stores a name and a list of {@link TrackerEntry} objects.
 */
public class Tracker implements Iterable<TrackerEntry> {

    private final String name;
    private final List<TrackerEntry> entries;
    private final Collection<TrackerListener> listeners = new ArrayList<>();


    /**
     * Initializes a {@link Tracker} with an empty list and the given name.
     *
     * @param name reference to the given Tracker.
     * @throws IllegalArgumentException if name is blank or argument is null.
     */
    public Tracker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.name = name;
        this.entries = new ArrayList<>();
    }

    /**
     * Adds a {@link TrackerEntry} with the current time to the list of trackerEntries.
     *
     * @param value to store a single input from user to the {@link TrackerEntry} to be added.
     * @throws IllegalArgumentException if the provided value is negative, or the timestamp is null.
     */
    public void addEntry(int value) {
        addEntry(value, Instant.now());
    }

    /**
     * Adds a {@link TrackerEntry} with a custom time to the list of trackerEntries.
     *
     * @param value     to store a single input from user to the {@link TrackerEntry}.
     * @param timestamp takes an Instant as a custom timestamp to store in the {@link TrackerEntry}.
     * @throws IllegalArgumentException if the provided value is negative, or the timestamp is null.
     */
    public void addEntry(int value, Instant timestamp) {
        if (value < 0) {
            throw new IllegalArgumentException("Entry value cannot be negative");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("The provided timestamp cannot be null.");
        }
        this.entries.add(new TrackerEntry(value, timestamp));
        notifyListeners();
    }

    /**
     * Removes the TrackerEntry most recently added to the end of the list. The order is guaranteed by the
     * fact that a List is being used.
     *
     * @throws IllegalStateException if the Tracker is empty
     */
    public void undoEntry() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot undo entry on an empty Tracker");
        }
        this.entries.remove(entries.size() - 1);
        notifyListeners();
    }

    /**
     * Uses an {@link EntrySummer} to sum all the {@link TrackerEntry} values that
     * satisfies a given predicate.
     *
     * @param predicate for filtering the {@link Tracker} by the given predicate.
     * @return the sum of all values in the list of TrackerEntry objects satisfied by the given
     *     predicate.
     */
    public int sumEntries(Predicate<TrackerEntry> predicate) {
        return EntrySummer.entrySum(this, predicate);
    }

    /**
     * Checks if the list of {@link TrackerEntry} objects is empty.
     *
     * @return true if the list of {@link TrackerEntry} objects is empty, otherwise false.
     */
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public String getName() {
        return name;
    }

    /**
     * Initializes a shallow copy of the List of {@link TrackerEntry} objects.
     *
     * @return a {@link List} containing {@link TrackerEntry} objects.
     */
    public List<TrackerEntry> getEntries() {
        return new ArrayList<>(entries);
    }


    @Override
    public Iterator<TrackerEntry> iterator() {
        return entries.iterator();
    }

    @Override
    public String toString() {
        return name;
    }


    /**
     * Notifies all listeners that implements {@link TrackerListener} and has been added to
     * the collection of listeners.
     */
    protected void notifyListeners() {
        this.listeners.forEach(listener -> listener.trackerChanged(this));
    }

    public void addListener(TrackerListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(TrackerListener listener) {
        if (listener != null) {
            this.listeners.remove(listener);
        }
    }

}
