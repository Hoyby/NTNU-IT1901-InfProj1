package ajour.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This class stores a list of {@link Tracker}'s.
 */
public class TrackerContainer implements Iterable<Tracker> {

    private final List<Tracker> trackers;

    /**
     * initializes a {@link TrackerContainer} with an empty list.
     */
    public TrackerContainer() {
        this.trackers = new ArrayList<>();
    }

    /**
     * Initializes a {@link TrackerContainer} with a list of {@link Tracker} objects.
     *
     * @param trackers list of {@link Tracker} objects.
     * @throws IllegalArgumentException if two or more of the {@link Tracker} objects has the
     *                                  same name.
     */
    public TrackerContainer(List<Tracker> trackers) {
        this();
        if (trackers.size() > trackers.stream().map(Tracker::getName).distinct().count()) {
            throw new IllegalArgumentException("Trackers must have unique names");
        }
        this.trackers.addAll(trackers);
    }

    /**
     * Adds a {@link Tracker} to the {@link TrackerContainer}.
     *
     * @param tracker to add to {@link TrackerContainer}
     * @throws IllegalArgumentException if a tracker {@link Tracker} with that name already exists
     */
    public void addTracker(Tracker tracker) {
        if (getTracker(tracker.getName()) != null) {
            throw new IllegalArgumentException("Tracker must have a unique name");
        }
        this.trackers.add(tracker);
    }

    /**
     * Performs a stream operation to contruct an {@link Optional} that contains a
     * {@link Tracker} if an object with the given name attribute exists within the underlying list.
     *
     * @param name used to reference the tracker to be returned if it exists
     * @return a {@link Optional} containing a {@link Tracker} if it exists
     */
    private Optional<Tracker> getOptionalTracker(String name) {
        return getTrackers()
            .stream()
            .filter(tracker -> tracker.getName().equals(name))
            .findFirst();
    }

    /**
     * Returns a {@link Tracker} from the list if it exists.
     *
     * @param name used to reference the tracker to be returned
     * @return the {@link Tracker} if it exists, otherwise null.
     */
    public Tracker getTracker(String name) {
        return getOptionalTracker(name).orElse(null);
    }

    /**
     * If a {@link Tracker} exists in the list, remove it.
     *
     * @param tracker the tracker to be removed
     */
    private void removeTracker(Tracker tracker) {
        if (trackers.contains(tracker)) {
            this.trackers.remove(tracker);
        }
    }

    /**
     * If a {@link Tracker} with the given name exists in the list, remove it.
     *
     * @param name used to reference the tracker to be removed
     */
    public void removeTracker(String name) {
        if (getOptionalTracker(name).isPresent()) {
            removeTracker(getOptionalTracker(name).get());
        }
    }

    /**
     * If a {@link Tracker} with the given name exists return true.
     *
     * @param name used to reference the tracker to assert
     * @return true if a {@link Tracker} with the given name exists
     */
    public boolean containsTracker(String name) {
        return getOptionalTracker(name).isPresent();
    }

    /**
     * Returns a shallow copy of the list of {@link Tracker} objects in this TrackerContainer.
     *
     * @return a {@link List} containing {@link Tracker} objects
     */
    public List<Tracker> getTrackers() {
        return new ArrayList<>(trackers);
    }


    @Override
    public Iterator<Tracker> iterator() {
        return trackers.iterator();
    }

    @Override
    public String toString() {
        return "TrackerContainer{"
            + trackers.toString()
            + '}';
    }
}
