package ajour.json;

import ajour.core.TrackerContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Saves TrackerContainers as a .json file.
 * Serialization and deserialization instructions for each core class is placed
 * in the serialization-classes with the same name + instruction.
 */

public class TrackerJson {

    private final ObjectMapper mapper;

    public TrackerJson() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new TrackerModule());
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void saveObject(Writer writer, TrackerContainer container) {
        try {
            mapper.writeValue(writer, container);
        } catch (IOException e) {
            System.out.println("The following error occurred when saving to file: '" + e.getMessage() + "'");
        }
    }

    public TrackerContainer loadObject(Reader reader) throws IOException {
        return mapper.readValue(reader, TrackerContainer.class);
    }
}
