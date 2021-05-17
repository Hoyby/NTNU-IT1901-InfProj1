package ajour.json;

import ajour.core.Tracker;
import ajour.core.TrackerEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;

public class TrackerDeserializer extends JsonDeserializer<Tracker> {

    private final TrackerEntryDeserializer trackerEntryDeserializer = new TrackerEntryDeserializer();

    @Override
    public Tracker deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode trackerNode = p.getCodec().readTree(p);
        return deserialize((JsonNode) trackerNode);
    }

    public Tracker deserialize(JsonNode node) {
        String name = node.get("name").asText();
        Tracker tracker = new Tracker(name);
        JsonNode entriesNode = node.get("entries");
        if (entriesNode instanceof ArrayNode) {
            for (JsonNode elementNode : entriesNode) {
                TrackerEntry item = trackerEntryDeserializer.deserialize(elementNode);
                if (item != null) {
                    tracker.addEntry(item.getValue(), item.getTimeStamp());
                }
            }
        }
        return tracker;
    }
}
