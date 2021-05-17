package ajour.ui;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import ajour.json.TrackerJson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TrackerContainerAccess} that uses a TrackerContainer for keeping
 * track of the different existing Tracker objects in the UI. This implementation serializes
 * the TrackerContainer and saves it to the path given by savePath.
 */

public class LocalTrackerContainerAccess implements TrackerContainerAccess {

    private TrackerContainer trackerContainer;
    private final Path savePath;

    /**
     * Sets the path to be used for saving and fetching, and sets up the TrackerContainer
     * to be used.
     *
     * @param savePath the path to used.
     */
    public LocalTrackerContainerAccess(Path savePath) {
        this.savePath = savePath;
        setTrackerContainer();
    }

    /**
     * Checks if there is already a file at the path given by savePath. If a new file is
     * created, it initializes a new TrackerContainer with default trackers. Otherwise loads
     * the TrackerContainer from savePath.
     */
    private void setTrackerContainer() {
        if (newFileCreated()) {
            this.trackerContainer = new TrackerContainer();
            saveToFile();
        } else {
            this.trackerContainer = loadFromFile();
        }
    }

    /**
     * Adds the Tracker to the underlying TrackerContainer.
     *
     * @param tracker the Tracker to be added.
     */
    @Override
    public void addTracker(Tracker tracker) {
        trackerContainer.addTracker(tracker);
        saveToFile();
    }

    /**
     * Removes the {@link Tracker} with the given name from the underlying {@link TrackerContainer}.
     *
     * @param name the name of the Tracker to be removed.
     */
    @Override
    public void removeTracker(String name) {
        trackerContainer.removeTracker(name);
        saveToFile();
    }

    /**
     * Constructs a {@link List} containing the names of all {@link Tracker}s in {@link TrackerContainer}.
     *
     * @return a {@link List} of Tracker names.
     */
    @Override
    public List<String> getTrackerNames() {
        return trackerContainer
            .getTrackers()
            .stream()
            .map(Tracker::getName)
            .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * Returns the {@link Tracker} object with the given name if it exists in the underlying
     * {@link TrackerContainer}, otherwise null.
     *
     * @param name the name of the {@link Tracker}.
     * @return the {@link Tracker} with the given name or null.
     */
    @Override
    public Tracker getTracker(String name) {
        return trackerContainer.getTracker(name);
    }

    /**
     * Checks if a {@link Tracker} of the given name exists within the underlying {@link TrackerContainer}.
     *
     * @param name the name of the {@link Tracker} to check for.
     * @return true if a {@link Tracker} with the given name exists, otherwise false.
     */
    @Override
    public boolean hasTracker(String name) {
        return this.trackerContainer.containsTracker(name);
    }

    /**
     * Used to notify that a {@link Tracker} within the underlying {@link TrackerContainer} has changed
     * and that the {@link TrackerContainer} needs to be saved to file.
     *
     * @param tracker the {@link Tracker} that changed.
     */
    @Override
    public void trackerChanged(Tracker tracker) {
        saveToFile();
    }

    /* Handling for loading and saving with JSON */

    /**
     * Saves the current state of the {@link TrackerContainer} by serializing and saving the result
     * as a JSON-file at the path given by savePath.
     */
    private void saveToFile() {
        TrackerJson trackerJson = new TrackerJson();
        try (Writer writer = new FileWriter(savePath.toFile(), StandardCharsets.UTF_8)) {
            trackerJson.saveObject(writer, trackerContainer);
        } catch (IOException e) {
            System.out.println("The following error occurred when saving to" + savePath.toString() + ": '" + e.getMessage() + "'");
        }
    }

    /**
     * Tries to load and parse a {@link TrackerContainer} from the JSON-file at the path given by
     * savePath.
     *
     * @return the parsed {@link TrackerContainer}. Otherwise null.
     */
    private TrackerContainer loadFromFile() {
        TrackerJson trackerJson = new TrackerJson();
        try (Reader reader = new FileReader(savePath.toFile(), StandardCharsets.UTF_8)) {
            return trackerJson.loadObject(reader);
        } catch (IOException e) {
            System.out.println("The following error occurred when saving to" + savePath.toString() + ": '" + e.getMessage() + "'");
            return null;
        }
    }

    /**
     * Creates a new file at the path given by savePath.
     *
     * @return true if a new file was created, false if the file already exists or a new
     *     file could not be created.
     */
    private Boolean newFileCreated() {
        try {
            Boolean newTrue = savePath.toFile().createNewFile();
            if (newTrue) {
                System.out.println("New save-file created");
            }
            return newTrue;
        } catch (IOException e) {
            System.out.println("Could not create save-file");
            return false;
        }
    }
}
