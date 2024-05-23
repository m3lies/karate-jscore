package ch.sku.karatescore.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class TimerService {
    private final IntegerProperty minutes = new SimpleIntegerProperty(0);
    private final IntegerProperty seconds = new SimpleIntegerProperty(0);
    private final IntegerProperty milliseconds = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds1 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds2 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds3 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalSeconds4 = new SimpleIntegerProperty(15);

    private final Timeline timeline;
    private final Timeline intervalTimeline;
    private final IntegerProperty period = new SimpleIntegerProperty(1);

    private final AudioClip shortBeep;
    private final AudioClip longBeep;

    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        intervalTimeline = new Timeline(new KeyFrame(Duration.millis(10), e -> decrementIntervalTime()));
        intervalTimeline.setCycleCount(Timeline.INDEFINITE);

        shortBeep = new AudioClip(getClass().getResource("/sounds/short-beep.mp3").toString());
        longBeep = new AudioClip(getClass().getResource("/sounds/long-beep.mp3").toString());
    }

    private Timeline createIntervalTimeline(IntegerProperty intervalSeconds, int targetPeriod) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            if (period.get() == targetPeriod && intervalSeconds.get() > 0) {
                if (milliseconds.get() > 0) {
                    milliseconds.set(milliseconds.get() - 1);
                } else {
                    milliseconds.set(99);
                    intervalSeconds.set(intervalSeconds.get() - 1);
                }
                if (intervalSeconds.get() == 0 && milliseconds.get() == 0) {
                    stopIntervalTimer(targetPeriod);
                    longBeep.play();
                    nextPeriod(); // Move to the next period
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
        stopAllIntervalTimers();
    }

    private void stopIntervalTimer(int period) {
        switch (period) {
            case 1:
                intervalTimeline.stop();
                break;
            case 2:
                intervalTimeline.stop();
                break;
            case 3:
                intervalTimeline.stop();
                break;
            case 4:
                intervalTimeline.stop();
                break;
        }
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public IntegerProperty secondsProperty() {
        return seconds;
    }

    public IntegerProperty millisecondsProperty() {
        return milliseconds;
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
        lastSetTimeInSeconds = totalSeconds;
        minutes.set(totalSeconds / 60);
        seconds.set(totalSeconds % 60);
        milliseconds.set(0);
    }

    public void setUpTimer(int mins, int secs) {
        setTimer(mins * 60 + secs);
    }

    private void decrementTime() {
        if (minutes.get() == 0 && seconds.get() == 0 && milliseconds.get() == 0) {
            stop();
            longBeep.play();
        } else if (seconds.get() == 0 && milliseconds.get() == 0) {
            minutes.set(minutes.get() - 1);
            seconds.set(59);
            milliseconds.set(99);
        } else if (milliseconds.get() == 0) {
            seconds.set(seconds.get() - 1);
            milliseconds.set(99);
        } else {
            milliseconds.set(milliseconds.get() - 1);
        }

        if (minutes.get() == 0 && seconds.get() == 0 && milliseconds.get() == 50) {
            shortBeep.play();
        }
    }

    private void decrementIntervalTime() {
        IntegerProperty intervalSeconds = getCurrentIntervalSeconds();
        if (intervalSeconds == null) {
            return;
        }

        if (intervalSeconds.get() == 0 && milliseconds.get() == 0) {
            stopAllIntervalTimers();
            longBeep.play();
            nextPeriod();
        } else if (milliseconds.get() == 0) {
            intervalSeconds.set(intervalSeconds.get() - 1);
            milliseconds.set(99);
        } else {
            milliseconds.set(milliseconds.get() - 1);
        }
    }

    private IntegerProperty getCurrentIntervalSeconds() {
        switch (period.get()) {
            case 1:
                return intervalSeconds1;
            case 2:
                return intervalSeconds2;
            case 3:
                return intervalSeconds3;
            case 4:
                return intervalSeconds4;
            default:
                return null;
        }
    }

    public void startIntervalTimer(int period) {
        this.period.set(period);
        stopAllIntervalTimers();
        intervalTimeline.play();
    }

    public void stopAllIntervalTimers() {
        intervalTimeline.stop();
    }

    public void resetInterval() {
        intervalSeconds1.set(15);
        intervalSeconds2.set(15);
        intervalSeconds3.set(15);
        intervalSeconds4.set(15);
        period.set(1);
    }

    public void resetIntervalForPeriod(int period) {
        switch (period) {
            case 1:
                intervalSeconds1.set(15);
                break;
            case 2:
                intervalSeconds2.set(15);
                break;
            case 3:
                intervalSeconds3.set(15);
                break;
            case 4:
                intervalSeconds4.set(15);
                break;
        }
    }

    public void nextPeriod() {
        int currentPeriod = period.get();
        int nextPeriod = (currentPeriod % 4) + 1;
        period.set(nextPeriod);
    }
}
