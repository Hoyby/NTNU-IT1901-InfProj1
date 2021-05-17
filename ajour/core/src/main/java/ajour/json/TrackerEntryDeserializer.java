package ajour.json;

import ajour.core.TrackerEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;

public class TrackerEntryDeserializer extends JsonDeserializer<TrackerEntry> {
    @Override
    public TrackerEntry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return deserialize(node);
    }

    public TrackerEntry deserialize(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        if (node instanceof ObjectNode) {
            try {
                return new TrackerEntry(
                    node.get("value").asInt()
                    , mapper.reader().readValue(node.get("timestamp"), Instant.class)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
