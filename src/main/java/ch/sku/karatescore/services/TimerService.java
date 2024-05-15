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
    private final IntegerProperty intervalSeconds1 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds2 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds3 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds4 = new SimpleIntegerProperty(15);

    private final Timeline timeline;
    private final Timeline intervalTimeline1;
    private final Timeline intervalTimeline2;
    private final Timeline intervalTimeline3;
    private final Timeline intervalTimeline4;
    private final IntegerProperty period = new SimpleIntegerProperty(1);

    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%02d:%02d", minutes.get(), seconds.get()), minutes, seconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        intervalTimeline1 = createIntervalTimeline(intervalSeconds1, 1);
        intervalTimeline2 = createIntervalTimeline(intervalSeconds2, 2);
        intervalTimeline3 = createIntervalTimeline(intervalSeconds3, 3);
        intervalTimeline4 = createIntervalTimeline(intervalSeconds4, 4);
    }

    private Timeline createIntervalTimeline(IntegerProperty intervalSeconds, int targetPeriod) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (period.get() == targetPeriod && intervalSeconds.get() > 0) {
                intervalSeconds.set(intervalSeconds.get() - 1);
                System.out.println("Period " + targetPeriod + " Timer: 00:" + intervalSeconds.get());
                if (intervalSeconds.get() == 0) {
                    stopIntervalTimer(targetPeriod);
                    period.set(period.get() % 4 + 1);
                    startIntervalTimer(period.get());
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);  // Ensure the timeline repeats indefinitely
        return timeline;
    }

    public void start() {
        System.out.println("Starting main timer");
        timeline.play();
        startIntervalTimer(period.get());  // Start the interval timer for the current period
    }

    public void stop() {
        System.out.println("Stopping main timer");
        timeline.stop();
        stopAllIntervalTimers();
    }

    private void stopIntervalTimer(int period) {
        switch (period) {
            case 1:
                intervalTimeline1.stop();
                break;
            case 2:
                intervalTimeline2.stop();
                break;
            case 3:
                intervalTimeline3.stop();
                break;
            case 4:
                intervalTimeline4.stop();
                break;
        }
        System.out.println("Stopped interval timer for period: " + period);
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public IntegerProperty secondsProperty() {
        return seconds;
    }

    public IntegerProperty intervalSecondsProperty1() {
        return intervalSeconds1;
    }

    public IntegerProperty intervalSecondsProperty2() {
        return intervalSeconds2;
    }

    public IntegerProperty intervalSecondsProperty3() {
        return intervalSeconds3;
    }

    public IntegerProperty intervalSecondsProperty4() {
        return intervalSeconds4;
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

    public void startIntervalTimer(int period) {
        System.out.println("Request to start interval timer for period: " + period);
        stopAllIntervalTimers();  // Ensure all other timers are stopped before starting the new one
        switch (period) {
            case 1:
                System.out.println("Starting interval timer for period 1");
                intervalTimeline1.play();
                break;
            case 2:
                System.out.println("Starting interval timer for period 2");
                intervalTimeline2.play();
                break;
            case 3:
                System.out.println("Starting interval timer for period 3");
                intervalTimeline3.play();
                break;
            case 4:
                System.out.println("Starting interval timer for period 4");
                intervalTimeline4.play();
                break;
        }
        System.out.println("Started interval timer for period: " + period);
    }

    public void stopAllIntervalTimers() {
        intervalTimeline1.stop();
        intervalTimeline2.stop();
        intervalTimeline3.stop();
        intervalTimeline4.stop();
        System.out.println("Stopped all interval timers");
    }

    public void resetInterval() {
        intervalSeconds1.set(15);
        intervalSeconds2.set(15);
        intervalSeconds3.set(15);
        intervalSeconds4.set(15);
        period.set(1);
        System.out.println("Reset all intervals and period to 1");
    }
}
