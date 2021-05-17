package ajour.json;

import ajour.core.Tracker;
import ajour.core.TrackerContainer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class TrackerContainerSerializer extends JsonSerializer<TrackerContainer> {
    @Override
    public void serialize(TrackerContainer container, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeArrayFieldStart("trackers");
        for (Tracker tracker : container) {
            gen.writeObject(tracker);
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
