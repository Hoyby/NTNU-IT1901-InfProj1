package ajour.json;

import ajour.core.TrackerContainer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public class TrackerContainerDeserializer extends JsonDeserializer<TrackerContainer> {

    private final TrackerDeserializer trackerDeserializer = new TrackerDeserializer();

    @Override
    public TrackerContainer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.getCodec().readTree(p);
        if (node instanceof ObjectNode) {
            ArrayNode containerNode = (ArrayNode) node.get("trackers");
            TrackerContainer container = new TrackerContainer();
            containerNode.forEach(trackerNode -> container.addTracker(trackerDeserializer.deserialize(trackerNode)));
            return container;
        }

        return null;
    }
}
