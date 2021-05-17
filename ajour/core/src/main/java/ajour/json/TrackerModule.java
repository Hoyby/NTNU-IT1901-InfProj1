package ajour.json;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import ajour.core.TrackerEntry;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class TrackerModule extends SimpleModule {

    private static final String NAME = "TrackerModule";

    public TrackerModule() {
        super(NAME, Version.unknownVersion());
        addSerializer(TrackerContainer.class, new TrackerContainerSerializer());
        addSerializer(Tracker.class, new TrackerSerializer());
        addSerializer(TrackerEntry.class, new TrackerEntrySerializer());
        addDeserializer(TrackerContainer.class, new TrackerContainerDeserializer());
        addDeserializer(Tracker.class, new TrackerDeserializer());
        addDeserializer(TrackerEntry.class, new TrackerEntryDeserializer());
    }
}
