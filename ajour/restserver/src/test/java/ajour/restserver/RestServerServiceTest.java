package ajour.restserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class RestServerServiceTest {

    @Autowired
    private RestServerService restServerService;

    @MockBean
    private RestServerPersistence restServerPersistence;


    @Test
    void testGetTrackerContainer() {
        TrackerContainer trackerContainer = new TrackerContainer();
        trackerContainer.addTracker(new Tracker("Test"));
        when(restServerPersistence.getContainerFromFile()).thenReturn(trackerContainer);
        assertNotNull(restServerService.getTrackerContainer());
    }

    @Test
    void testSaveTrackerContainer() {
        TrackerContainer trackerContainer = new TrackerContainer();
        trackerContainer.addTracker(new Tracker("Test"));
        when(restServerPersistence.saveContainerToFile(any(TrackerContainer.class))).thenReturn(true);
        assertTrue(restServerService.saveTrackerContainer());
    }

    @Test
    void testAddTracker() {
        String trackerName = "Test";
        Tracker tracker = new Tracker(trackerName);
        assertTrue(restServerService.addTracker(tracker));
    }

    @Test
    void testDeleteTracker() {
        String trackerName = "Test";
        Tracker tracker = new Tracker(trackerName);
        restServerService.addTracker(tracker);
        assertTrue(restServerService.deleteTracker(trackerName));
    }
}
