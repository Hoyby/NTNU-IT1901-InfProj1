package ajour.core.utility;

import ajour.core.Tracker;
import ajour.core.TrackerEntry;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Sums values from TrackerEntry-objects in a given time space.
 */

public class EntrySummer {

    public static final Predicate<TrackerEntry> today = (entry ->
            entry.getTimeStamp().isAfter(midnightInstant()));

    public static final Predicate<TrackerEntry> yesterday = (entry ->
            entry.getTimeStamp().isAfter(yesterdayMidnight()) && entry.getTimeStamp().isBefore(midnightInstant()));

    public static final Predicate<TrackerEntry> lastWeek = (entry ->
            entry.getTimeStamp().isAfter(lastWeekMidnight()));

    public static final Predicate<TrackerEntry> lastMonth = (entry ->
            entry.getTimeStamp().isAfter(lastMonthMidnight()));


    /**
     * sums all the {@link TrackerEntry} values in a {@link Tracker} that satisfies the given predicate.
     *
     * @param predicate for testing the content of a {@link Tracker} against the given predicate.
     * @param tracker   the {@link Tracker} containing the {@link TrackerEntry}'s to be filtered.
     * @return the sum of all values in the {@link TrackerEntry}'s in {@link Tracker} that satisfies the given predicate
     */
    public static int entrySum(Tracker tracker, Predicate<TrackerEntry> predicate) {
        if (tracker.isEmpty()) {
            return 0;
        }
        return tracker.getEntries()
                .stream()
                .filter(predicate)
                .mapToInt(TrackerEntry::getValue).sum();
    }


    public static List<TrackerEntry> getEntriesFromPred(Tracker tracker, Predicate<TrackerEntry> filter) {
        return tracker.getEntries()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /*
     * pre-made Instants to be passed on as predicate arguments based on the given time-space
     */
    private static Instant midnightInstant() {
        return Instant.now().truncatedTo(ChronoUnit.DAYS);
    }

    private static Instant yesterdayMidnight() {
        return midnightInstant().minus(1, ChronoUnit.DAYS);
    }

    private static Instant lastWeekMidnight() {
        return midnightInstant().minus(6, ChronoUnit.DAYS);
    }

    private static Instant lastMonthMidnight() {
        return midnightInstant().minus(30, ChronoUnit.DAYS);
    }
}