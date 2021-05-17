package ajour.ui;


import java.net.URI;
import java.nio.file.Path;
import javafx.fxml.FXML;

/**
 * Main controller for the app.
 */

public class AjourAppController {

    @FXML TrackerListViewController listViewController;

    /**
     * Initializes the containerAccess by checking the ajour.properties. If the appropriate key
     * is set, it will use RemoteTrackerContainerAccess as data access, otherwise local.
     */
    @FXML
    void initialize() {
        Configuration config = new Configuration();
        if (config.getProperty("remoteAccess").equals("true")) {
            listViewController.setTrackerDataAccess(
                    new RemoteTrackerContainerAccess(
                            URI.create(config.getProperty("serverUri"))
                    )
            );
        } else {
            listViewController.setTrackerDataAccess(new LocalTrackerContainerAccess(
                    Path.of(System.getProperty("user.home"), config.getProperty("savePath"))
            ));
        }
    }

}
