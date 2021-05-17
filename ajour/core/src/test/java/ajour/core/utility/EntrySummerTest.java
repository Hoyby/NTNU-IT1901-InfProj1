package ajour.core.utility;

import ajour.core.Tracker;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntrySummerTest {

    Tracker tracker1;
    Tracker tracker2;

    @BeforeEach
    public void setUp() {

        final Instant instantYesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        final Instant instantLastWeek = Instant.now().minus(7, ChronoUnit.DAYS);


        tracker1 = new Tracker("t2");
        tracker1.addEntry(10);
        tracker1.addEntry(11);
        tracker1.addEntry(12, instantYesterday);
        tracker1.addEntry(1, instantYesterday);
        tracker1.addEntry(13, instantLastWeek);

        tracker2 = new Tracker("t3");

    }

    @Test
    public void testEntrySummerMethods() {

        Assertions.assertEquals(21, tracker1.sumEntries(EntrySummer.today));
        Assertions.assertEquals(13, tracker1.sumEntries(EntrySummer.yesterday));
        Assertions.assertEquals(34, tracker1.sumEntries(EntrySummer.lastWeek));

        Assertions.assertEquals(0, tracker2.sumEntries(EntrySummer.today));

    }

}
