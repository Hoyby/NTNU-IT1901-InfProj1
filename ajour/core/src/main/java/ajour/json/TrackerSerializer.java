package ajour.json;

import ajour.core.Tracker;
import ajour.core.TrackerEntry;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class TrackerSerializer extends JsonSerializer<Tracker> {

    @Override
    public void serialize(Tracker tracker, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", tracker.getName());
        gen.writeArrayFieldStart("entries");
        for (TrackerEntry entry : tracker) {
            gen.writeObject(entry);
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
