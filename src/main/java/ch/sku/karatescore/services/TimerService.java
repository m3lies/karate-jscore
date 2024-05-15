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

    private final Timeline intervalTimeline;
    private final IntegerProperty period = new SimpleIntegerProperty(1);

    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%02d:%02d", minutes.get(), seconds.get()), minutes, seconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        intervalTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementTime()));
        intervalTimeline.setCycleCount(Timeline.INDEFINITE);
        intervalTimeline1 = createIntervalTimeline(intervalSeconds1, 1);
        intervalTimeline2 = createIntervalTimeline(intervalSeconds2, 2);
        intervalTimeline3 = createIntervalTimeline(intervalSeconds3, 3);
        intervalTimeline4 = createIntervalTimeline(intervalSeconds4, 4);


        // Removed the reset call from the constructor
    }
    private Timeline createIntervalTimeline(IntegerProperty intervalSeconds, int targetPeriod) {
        return new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (period.get() == targetPeriod && intervalSeconds.get() > 0) {
                intervalSeconds.set(intervalSeconds.get() - 1);
                if (intervalSeconds.get() == 0) {
                    stopIntervalTimer(targetPeriod);
                    period.set(period.get() % 4 + 1);
                    startIntervalTimer(period.get());
                }
            }
        }));
    }
    private Timeline setupIntervalTimeline(IntegerProperty intervalSeconds, IntegerProperty period) {
        return new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (intervalSeconds.get() > 0) {
                intervalSeconds.set(intervalSeconds.get() - 1);
            } else {
                intervalSeconds.set(15);
                period.set((period.get() % 4) + 1); // Cycle through 4 periods
            }
        }));
    }

    public void start() {
        timeline.play();
    }

    // New method to reset and start the timer
    public void resetAndStart() {
        reset();
        start();
    }

    public void stop() {
        timeline.stop();
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
    private void resetPeriod() {
        period.set(1);
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
        switch (period) {
            case 1:
                intervalTimeline1.play();
                break;
            case 2:
                intervalTimeline2.play();
                break;
            case 3:
                intervalTimeline3.play();
                break;
            case 4:
                intervalTimeline4.play();
                break;
        }
    }

    public void stopIntervalTimer() {
        intervalTimeline.stop();
    }


}
