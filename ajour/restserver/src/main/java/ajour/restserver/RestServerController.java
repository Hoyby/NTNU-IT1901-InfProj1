package ajour.restserver;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for the server application that handles GET, PUT and DELETE mappings.
 */
@RestController
@RequestMapping(RestServerController.TRACKER_CONTAINER_SERVICE_PATH)
public class RestServerController {

    public static final String TRACKER_CONTAINER_SERVICE_PATH = "ajour";

    @Autowired
    private RestServerService restServerService;

    /**
     * When sent a GET mapping with the service path but no additional path it will send the current
     * TrackerContainer from the RestServerService and return it in the response.
     *
     * @return the current TrackerContainer
     */
    @GetMapping
    public TrackerContainer getTrackerContainer() {
        return restServerService.getTrackerContainer();
    }


    /**
     * Gets the Tracker with the given name from the server.
     *
     * @param name the name of the tracker to be fetched.
     * @return a Tracker with the given name.
     */
    @GetMapping(path = "/{name}")
    public Tracker getTracker(@PathVariable("name") String name) {
        String decodedName = decodeName(name);
        return restServerService.getTracker(decodedName);
    }

    /**
     * Adds or replaces a Tracker in the TrackerContainer used by the server.
     *
     * @param name    the name of the Tracker to be added or replaced.
     * @param tracker the Tracker that is to be added or replaced.
     * @return true if the Tracker was added or replaced, otherwise false
     */
    @PutMapping(path = "/{name}")
    public boolean putTracker(@PathVariable("name") String name, @RequestBody Tracker tracker) {
        String decodedName = decodeName(name);
        restServerService.deleteTracker(decodedName);
        if (restServerService.addTracker(tracker)) {
            restServerService.saveTrackerContainer();
            return true;
        }
        return false;
    }

    /**
     * Deletes the Tracker with the given name from the server.
     *
     * @param name the name of the Tracker to be deleted
     * @return true if the Tracker was deleted, otherwise false
     */
    @DeleteMapping(path = "/{name}")
    public boolean deleteTracker(@PathVariable("name") String name) {
        String decodedName = decodeName(name);
        if (restServerService.deleteTracker(decodedName)) {
            restServerService.saveTrackerContainer();
            return true;
        }
        return false;
    }

    /**
     * Utility method used for passing through the names in the different mappings, in order to decode
     * + and %20 into spaces, so that the correct Tracker is added/deleted/returned.
     *
     * @param name the name to decode
     * @return the decoded name
     */
    private String decodeName(String name) {
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }
}
