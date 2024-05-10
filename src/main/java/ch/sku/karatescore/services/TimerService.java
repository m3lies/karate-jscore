package ch.sku.karatescore.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimerService {
    private final IntegerProperty minutes = new SimpleIntegerProperty(0);
    private final IntegerProperty seconds = new SimpleIntegerProperty(0);
    private final Timeline timeline;
    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timeLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format("%02d:%02d", minutes.get(), seconds.get()),
                minutes, seconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e-> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        reset();
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public IntegerProperty secondsProperty() {
        return seconds;
    }
    public void reset() {
        setTimer(lastSetTimeInSeconds);
    }

    public void setTimer(int totalSeconds) {
        System.out.println("Setting timer: " + totalSeconds);
        lastSetTimeInSeconds = totalSeconds;
        minutes.set(totalSeconds / 60);
        seconds.set(totalSeconds % 60);
        System.out.println("Minutes set to: " + minutes.get() + ", Seconds set to: " + seconds.get());
    }

    public void setUpTimer(int mins, int secs) {
        setTimer(mins * 60 + secs);
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
}
