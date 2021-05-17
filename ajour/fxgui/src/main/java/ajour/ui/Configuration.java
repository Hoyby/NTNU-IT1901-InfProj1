package ajour.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Initializes a Properties object by loading from ajour.properties in src/resources,
 * which will enable having different savepaths and configurations for main and test.
 */

public class Configuration {

    private final Properties properties;

    public Configuration() {
        properties = new Properties();

        if (isIntegrationTest()) {
            try (InputStream inputStream = new FileInputStream("src/test/resources/ajour/ui/ajour.properties")) {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (InputStream inputStream = getClass().getResourceAsStream("ajour.properties")) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IllegalStateException("ajour.properties could not be loaded");
            }
        }
    }

    /**
     * Returns the value of the provided key from ajour.properties.
     *
     * @param key the name of the key.
     * @return the value for the given key.
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Checks the system property set during the integration test in order to load the correct
     * properties file. (Which in the integration test will be located outside the .jar for this
     * module.
     *
     * @return true if the correct variable is set, false otherwise.
     */
    private boolean isIntegrationTest() {
        try {
            return System.getProperty("ajour.it").equals("true");
        } catch (Exception e) {
            return false;
        }
    }
}
