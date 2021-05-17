package ajour.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RestServerPersistenceTest {

    @Autowired
    private RestServerPersistence restServerPersistence;

    @Autowired
    private Environment environment;

    @Test
    void testGetContainerFromFile() {
        final String testName1 = "Trening";
        final String testName2 = "test";
        TrackerContainer initialContainer = new TrackerContainer();
        initialContainer.addTracker(new Tracker(testName1));
        initialContainer.addTracker(new Tracker("test"));
        initialContainer.getTracker(testName1).addEntry(4);
        restServerPersistence.saveContainerToFile(initialContainer);
        TrackerContainer trackerContainer = restServerPersistence.getContainerFromFile();
        System.out.println(trackerContainer);
        assertTrue(trackerContainer.containsTracker(testName1));
        assertTrue(trackerContainer.containsTracker(testName2));

        assertEquals(4, trackerContainer.getTracker(testName1).sumEntries(trackerEntry -> true));


    }

    @Test
    void testSaveContainerToFile() {
        String trackerName = "saveContainerTest";
        TrackerContainer trackerContainer = new TrackerContainer();
        trackerContainer.addTracker(new Tracker(trackerName));
        restServerPersistence.saveContainerToFile(trackerContainer);

        assertTrue(Path.of(System.getProperty("user.home"), environment.getProperty("savePath")).toFile().exists());
        assertTrue(restServerPersistence.getContainerFromFile().containsTracker(trackerName));
    }

    @AfterEach
    void tearDown() {
        Path.of(System.getProperty("user.home"), environment.getProperty("savePath")).toFile().delete();
    }
}
