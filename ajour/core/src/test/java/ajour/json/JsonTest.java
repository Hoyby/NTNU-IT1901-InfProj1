package ajour.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonTest {

    TrackerContainer trackerContainerToFile;
    final Path jsonTestPath = Path.of("target\\JsonTest.json");

    @BeforeEach
    public void setUp() {
        Instant instantYesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant instantLastWeek = Instant.now().minus(7, ChronoUnit.DAYS);

        int t1 = 10;
        int t2 = 11;
        int t3 = 12;
        int t4 = 1;
        int t5 = 13;

        Tracker tracker1 = new Tracker("t2");
        tracker1.addEntry(t1);
        tracker1.addEntry(t2);
        tracker1.addEntry(t3, instantYesterday);
        tracker1.addEntry(t4, instantYesterday);
        tracker1.addEntry(t5, instantLastWeek);

        Tracker tracker2 = new Tracker("t3");
        trackerContainerToFile = new TrackerContainer(Arrays.asList(tracker1, tracker2));
    }

    @Test
    public void testJsonIO() throws IOException {

        try {
            jsonTestPath.toFile().createNewFile();
        } catch (IOException e) {
            fail();
        }

        TrackerJson trackerJson = new TrackerJson();
        trackerJson.saveObject(new FileWriter(jsonTestPath.toFile()), trackerContainerToFile);

        TrackerContainer trackerContainerFromFile = trackerJson.loadObject(new FileReader(jsonTestPath.toFile()));
        assertEquals(trackerContainerToFile.toString(), trackerContainerFromFile.toString());

        jsonTestPath.toFile().delete();
    }
}
