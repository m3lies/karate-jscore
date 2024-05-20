package ch.sku.karatescore.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.AudioClip;
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

    private final AudioClip shortBeep;
    private final AudioClip longBeep;

    private int lastSetTimeInSeconds = 0;

    public TimerService() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> decrementTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        intervalTimeline1 = createIntervalTimeline(intervalSeconds1, 1);
        intervalTimeline2 = createIntervalTimeline(intervalSeconds2, 2);
        intervalTimeline3 = createIntervalTimeline(intervalSeconds3, 3);
        intervalTimeline4 = createIntervalTimeline(intervalSeconds4, 4);

        shortBeep = new AudioClip(getClass().getResource("/sounds/short-beep.mp3").toString());
        longBeep = new AudioClip(getClass().getResource("/sounds/long-beep.mp3").toString());

    }



    private Timeline createIntervalTimeline(IntegerProperty intervalSeconds, int targetPeriod) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (period.get() == targetPeriod && intervalSeconds.get() > 0) {
                intervalSeconds.set(intervalSeconds.get() - 1);
                if (intervalSeconds.get() == 0) {
                    stopIntervalTimer(targetPeriod);
                    longBeep.play();
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

    public void setTimer(int totalSeconds) {
        lastSetTimeInSeconds = totalSeconds;
        minutes.set(totalSeconds / 60);
        seconds.set(totalSeconds % 60);
    }

    public void setUpTimer(int mins, int secs) {
        setTimer(mins * 60 + secs);
    }

    private void decrementTime() {
        if (seconds.get() == 0 && minutes.get() == 0) {
            stop();
            longBeep.play();
        } else if (seconds.get() == 0) {
            minutes.set(minutes.get() - 1);
            seconds.set(59);
        } else {
            seconds.set(seconds.get() - 1);
        }

        if (minutes.get() == 0 && seconds.get() == 15) {
            System.out.println("Short beep should play now");
            shortBeep.play();
        }
    }

    public void startIntervalTimer(int period) {
        stopAllIntervalTimers();
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

    public void stopAllIntervalTimers() {
        intervalTimeline1.stop();
        intervalTimeline2.stop();
        intervalTimeline3.stop();
        intervalTimeline4.stop();
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
