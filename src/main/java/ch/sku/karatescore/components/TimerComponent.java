package ch.sku.karatescore.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class TimerComponent extends HBox {
    private final IntegerProperty seconds = new SimpleIntegerProperty(0);
    private final IntegerProperty minutes = new SimpleIntegerProperty(0);
    private final Timeline timeline;
    private final Label timeLabel = new Label();
    private int lastSetTimeInSeconds = 0;

    public TimerComponent() {
        this.setStyle("-fx-background-color: lightblue; -fx-padding: 10px; -fx-border-color: black; -fx-border-width: 2px;");
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timeLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format("%02d:%02d", minutes.get(), seconds.get()),
                minutes, seconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e-> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(timeLabel);

        reset();
    }

    private void decrementTime() {
        if (seconds.get() == 0 && minutes.get() == 0) {
            stop();
        } else if (seconds.get() == 0) {
            minutes.set(minutes.get() - 1);
            seconds.set(59);
        } else {
            seconds.set(seconds.get() - 1);
        }
    }
    // Other methods remain the same


    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    public void reset() {
        setTimer(lastSetTimeInSeconds);
    }

    public void setTimer(int totalSeconds) {
        lastSetTimeInSeconds = totalSeconds;
        minutes.set(totalSeconds / 60);
        seconds.set(totalSeconds % 60);
    }

    public void setUpTimer(int mins, int secs) {
        setTimer(mins * 60 + secs);
    }

}
