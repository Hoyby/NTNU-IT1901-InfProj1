package ajour.restserver;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that keeps provides the TrackerContainer and the needed methods to allow the controller
 * to serve the different mapping defined.
 */
@Service
public class RestServerService {

    @Autowired
    private RestServerPersistence restServerPersistence;

    private TrackerContainer trackerContainer;


    protected TrackerContainer getTrackerContainer() {
        return trackerContainer;
    }

    protected boolean saveTrackerContainer() {
        return restServerPersistence.saveContainerToFile(trackerContainer);
    }

    protected Tracker getTracker(String name) {
        return trackerContainer.getTracker(name);
    }

    protected boolean addTracker(Tracker tracker) {
        try {
            trackerContainer.addTracker(tracker);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    protected boolean deleteTracker(String name) {
        if (trackerContainer.containsTracker(name)) {
            trackerContainer.removeTracker(name);
            return true;
        }
        return false;
    }


    /**
     * In order for the Service to be in the correct state for the controller to use, this method initializes
     * the TrackerContainer after the Autowired injection has been done.
     */
    @PostConstruct
    private void init() {
        trackerContainer = restServerPersistence.getContainerFromFile();
        if (trackerContainer == null) {
            trackerContainer = new TrackerContainer();
            saveTrackerContainer();
            System.out.println("Could not read or find valid savedata, " + "creating new TrackerContainer instead");
        }
    }
}
