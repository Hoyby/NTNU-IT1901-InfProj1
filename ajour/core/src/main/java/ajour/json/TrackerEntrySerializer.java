package ajour.json;

import ajour.core.TrackerEntry;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class TrackerEntrySerializer extends JsonSerializer<TrackerEntry> {
    @Override
    public void serialize(TrackerEntry entry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("value", entry.getValue());
        gen.writeObjectField("timestamp", entry.getTimeStamp());
        gen.writeEndObject();
    }
}
