package ajour.ui;

import ajour.core.Tracker;
import java.util.List;

/**
 * Interface for abstracting the access to data in the controller.
 */

public interface TrackerContainerAccess {

    /**
     * Adds the given Tracker to the underlying container.
     *
     * @param tracker the Tracker to be added
     */
    void addTracker(Tracker tracker);

    /**
     * Removes the Tracker with the given name from the underlying container.
     *
     * @param name the name of the Tracker to be removed.
     */
    void removeTracker(String name);

    /**
     * Fetches the names of all Trackers within the implementation. Returned as a list
     * directly for use in the ListView object.
     *
     * @return a List containing the names of all Trackers.
     */
    List<String> getTrackerNames();

    /**
     * Returns the Tracker with the given name if it exists.
     *
     * @param name the name of the Tracker.
     * @return the Tracker object with the given name, otherwise null.
     */
    Tracker getTracker(String name);

    /**
     * Checks whether there exists a Tracker with the given name.
     *
     * @param name the name of the Tracker
     * @return true if a Tracker with the given name exists, otherwise false.
     */
    boolean hasTracker(String name);

    void trackerChanged(Tracker tracker);
}
