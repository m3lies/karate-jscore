package ch.sku.karatescore.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimerService {
    private final IntegerProperty minutes = new SimpleIntegerProperty(0);
    private final IntegerProperty seconds = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds = new SimpleIntegerProperty(15);
    private final Timeline intervalTimeline;
    private final IntegerProperty period = new SimpleIntegerProperty(1);

    private final Timeline timeline;
    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%02d:%02d", minutes.get(), seconds.get()), minutes, seconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        intervalTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementIntervalTime()));
        intervalTimeline.setCycleCount(Timeline.INDEFINITE);
        resetInterval();

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

    public IntegerProperty intervalSecondsProperty() {
        return intervalSeconds;
    }

    public IntegerProperty periodProperty() {
        return period;
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

    private void decrementIntervalTime() {
        if (intervalSeconds.get() > 0) {
            intervalSeconds.set(intervalSeconds.get() - 1);
        } else {
            stopIntervalTimer();
            intervalSeconds.set(15);  // Reset the countdown every 15 seconds
            if (period.get() <=4) {
                period.set(period.get() + 1);  // Increment period up to 4
            }
            if (period.get() > 4) {
                resetInterval();  // Optional: Stop the interval timer after 4 periods
            }
        }
    }

    public void startIntervalTimer() {
        intervalTimeline.play();
    }

    public void stopIntervalTimer() {
        intervalTimeline.stop();
    }

    public void resetInterval() {
        intervalSeconds.set(15);
        period.set(1);
    }


}
