package ajour.restserver;

import ajour.core.TrackerContainer;
import ajour.json.TrackerJson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * Component class used by the RestServerService to store the TrackerContainer, so that the state can be preserved
 * through restarts.
 */
@Component
public class RestServerPersistence implements EnvironmentAware {

    @Autowired
    private Environment environment;

    private Path savePath;

    protected TrackerContainer getContainerFromFile() {
        TrackerJson trackerJson = new TrackerJson();
        try (Reader reader = new FileReader(savePath.toFile(), StandardCharsets.UTF_8)) {
            return trackerJson.loadObject(reader);
        } catch (IOException e) {
            createNewFile();
        }
        return null;
    }

    protected boolean saveContainerToFile(TrackerContainer trackerContainer) {
        TrackerJson trackerJson = new TrackerJson();
        try (Writer writer = new FileWriter(savePath.toFile(), StandardCharsets.UTF_8)) {
            trackerJson.saveObject(writer, trackerContainer);
            return true;
        } catch (IOException e) {
            System.out.println("The following error occurred when saving to" + savePath + ": '" + e.getMessage() + "'");
        }
        return false;
    }

    private void createNewFile() {
        try {
            if (savePath.toFile().createNewFile()) {
                System.out.println(savePath + " created");
            }
        } catch (IOException e) {
            System.out.println("Could not create " + savePath);
        }
    }

    @PostConstruct
    private void init() {
        savePath = Path.of(System.getProperty("user.home"), environment.getProperty("savePath"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
