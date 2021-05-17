package ajour.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller class for the clock.
 */

public class ClockController {

    @FXML Text appClock;

    @FXML
    void initialize() {
        clock();
    }

    public void clock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
            appClock.setText(dateFormat.format(new Date()));
        }), new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
