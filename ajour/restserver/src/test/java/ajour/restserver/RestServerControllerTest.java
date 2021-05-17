package ajour.restserver;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import ajour.core.TrackerEntry;
import ajour.json.TrackerModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class RestServerControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    RestServerService restServerService;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModules(new TrackerModule(), new JavaTimeModule());
    private final String servicePath = "/ajour/";

    @Test
    void testGetTrackerContainer() throws Exception {
        TrackerContainer trackerContainer = new TrackerContainer();
        trackerContainer.addTracker(new Tracker("Test"));
        when(restServerService.getTrackerContainer()).thenReturn(trackerContainer);

        mockMvc.perform(get(servicePath))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trackers", Matchers.not(empty())))
            .andExpect(jsonPath("$.trackers[0].name", equalTo("Test")));
    }

    @Test
    void testGetTracker() throws Exception {
        String trackerName = "test";
        Tracker tracker = new Tracker(trackerName);
        when(restServerService.getTracker(any(String.class))).thenReturn(tracker);

        mockMvc.perform(get(servicePath + trackerName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", equalTo(trackerName)))
            .andExpect(jsonPath("$.entries", Matchers.is(emptyCollectionOf(TrackerEntry.class))));
    }

    @Test
    void testPutTracker() throws Exception {
        String trackerName = "Test";
        Tracker tracker = new Tracker(trackerName);
        String json = objectMapper.writeValueAsString(tracker);
        when(restServerService.addTracker(any(Tracker.class))).thenReturn(true);

        mockMvc.perform(put(servicePath + trackerName)
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", equalTo(true)));

        when(restServerService.addTracker(any(Tracker.class))).thenReturn(false);

        mockMvc.perform(put(servicePath + trackerName)
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
            .content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", equalTo(false)));

    }

    @Test
    void testDeleteTracker() throws Exception {
        String trackerName = "test";
        Tracker tracker = new Tracker(trackerName);
        when(restServerService.getTracker(any(String.class))).thenReturn(tracker);
        when(restServerService.deleteTracker(any(String.class))).thenReturn(true);
        mockMvc.perform(delete(servicePath + trackerName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", equalTo(true)));
    }
}
