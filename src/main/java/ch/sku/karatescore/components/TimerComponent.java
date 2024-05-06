package ch.sku.karatescore.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


public class TimerComponent extends HBox {
    private final IntegerProperty seconds = new SimpleIntegerProperty();
    private final IntegerProperty minutes = new SimpleIntegerProperty();
    private final Timeline timeline;

    public TimerComponent() {
        // Initialize time label
        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px;");

        // Initialize timeline for countdown
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (seconds.get() == 0 && minutes.get() == 0) {
                    stop();
                } else if (seconds.get() == 0) {
                    seconds.set(59);
                    minutes.set(minutes.get() - 1);
                } else {
                    seconds.set(seconds.get() - 1);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        StringProperty timeText = new SimpleStringProperty();
        timeLabel.textProperty().bind(timeText);

        // Set alignment and add time label to the TimerComponent
        setAlignment(Pos.CENTER);
        getChildren().add(timeLabel);
    }

    // Start the timer
    public void start() {
        timeline.play();
    }

    // Stop the timer
    public void stop() {
        timeline.stop();
    }

    // Pause the timer
    public void pause() {
        timeline.pause();
    }

    // Reset the timer to a specific time (in seconds)
    public void reset(int totalSeconds) {
        minutes.set(totalSeconds / 60);
        seconds.set(totalSeconds % 60);
    }

    // Set up the timer with a specific time (in seconds)
    public void setUpTimer(int totalSeconds) {
        reset(totalSeconds);
    }

}

