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

    private final IntegerProperty intervalMinutes1 = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds1 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalMilliseconds1 = new SimpleIntegerProperty(0);

    private final IntegerProperty intervalMinutes2 = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds2 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalMilliseconds2 = new SimpleIntegerProperty(0);

    private final IntegerProperty intervalMinutes3 = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds3 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalMilliseconds3 = new SimpleIntegerProperty(0);

    private final IntegerProperty intervalMinutes4 = new SimpleIntegerProperty(0);
    private final IntegerProperty intervalSeconds4 = new SimpleIntegerProperty(15);
    private final IntegerProperty intervalMilliseconds4 = new SimpleIntegerProperty(0);

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

        shortBeep = new AudioClip(getClass().getResource("/sounds/NewBeepNov.mp3").toString());
        longBeep = new AudioClip(getClass().getResource("/sounds/NewLongBeepNov.mp3").toString());
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
        stopAllIntervalTimers();
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

    public IntegerProperty intervalMinutesProperty1() {
        return intervalMinutes1;
    }

    public IntegerProperty intervalSecondsProperty1() {
        return intervalSeconds1;
    }

    public IntegerProperty intervalMillisecondsProperty1() {
        return intervalMilliseconds1;
    }

    public IntegerProperty intervalMinutesProperty2() {
        return intervalMinutes2;
    }

    public IntegerProperty intervalSecondsProperty2() {
        return intervalSeconds2;
    }

    public IntegerProperty intervalMillisecondsProperty2() {
        return intervalMilliseconds2;
    }

    public IntegerProperty intervalMinutesProperty3() {
        return intervalMinutes3;
    }

    public IntegerProperty intervalSecondsProperty3() {
        return intervalSeconds3;
    }

    public IntegerProperty intervalMillisecondsProperty3() {
        return intervalMilliseconds3;
    }

    public IntegerProperty intervalMinutesProperty4() {
        return intervalMinutes4;
    }

    public IntegerProperty intervalSecondsProperty4() {
        return intervalSeconds4;
    }

    public IntegerProperty intervalMillisecondsProperty4() {
        return intervalMilliseconds4;
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

        if (minutes.get() == 0 && seconds.get() == 15 && milliseconds.get() == 99) {
            shortBeep.play();
        }
    }

    private void decrementIntervalTime() {
        IntegerProperty intervalMinutes = getCurrentIntervalMinutes();
        IntegerProperty intervalSeconds = getCurrentIntervalSeconds();
        IntegerProperty intervalMilliseconds = getCurrentIntervalMilliseconds();

        if (intervalMinutes == null || intervalSeconds == null || intervalMilliseconds == null) {
            return;
        }

        if (intervalMinutes.get() == 0 && intervalSeconds.get() == 0 && intervalMilliseconds.get() == 0) {
            stopAllIntervalTimers();
            longBeep.play();
            nextPeriod();
        } else if (intervalSeconds.get() == 0 && intervalMilliseconds.get() == 0) {
            intervalMinutes.set(intervalMinutes.get() - 1);
            intervalSeconds.set(59);
            intervalMilliseconds.set(99);
        } else if (intervalMilliseconds.get() == 0) {
            intervalSeconds.set(intervalSeconds.get() - 1);
            intervalMilliseconds.set(99);
        } else {
            intervalMilliseconds.set(intervalMilliseconds.get() - 1);
        }
    }

    private IntegerProperty getCurrentIntervalMinutes() {
        switch (period.get()) {
            case 1:
                return intervalMinutes1;
            case 2:
                return intervalMinutes2;
            case 3:
                return intervalMinutes3;
            case 4:
                return intervalMinutes4;
            default:
                return null;
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

    private IntegerProperty getCurrentIntervalMilliseconds() {
        switch (period.get()) {
            case 1:
                return intervalMilliseconds1;
            case 2:
                return intervalMilliseconds2;
            case 3:
                return intervalMilliseconds3;
            case 4:
                return intervalMilliseconds4;
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
        intervalMinutes1.set(0);
        intervalSeconds1.set(15);
        intervalMilliseconds1.set(0);

        intervalMinutes2.set(0);
        intervalSeconds2.set(15);
        intervalMilliseconds2.set(0);

        intervalMinutes3.set(0);
        intervalSeconds3.set(15);
        intervalMilliseconds3.set(0);

        intervalMinutes4.set(0);
        intervalSeconds4.set(15);
        intervalMilliseconds4.set(0);

        period.set(1);
    }

    public void resetIntervalForPeriod(int period) {
        switch (period) {
            case 1:
                intervalMinutes1.set(0);
                intervalSeconds1.set(15);
                intervalMilliseconds1.set(0);
                break;
            case 2:
                intervalMinutes2.set(0);
                intervalSeconds2.set(15);
                intervalMilliseconds2.set(0);
                break;
            case 3:
                intervalMinutes3.set(0);
                intervalSeconds3.set(15);
                intervalMilliseconds3.set(0);
                break;
            case 4:
                intervalMinutes4.set(0);
                intervalSeconds4.set(15);
                intervalMilliseconds4.set(0);
                break;
        }
    }

    public void nextPeriod() {
        int currentPeriod = period.get();
        int nextPeriod = (currentPeriod % 4) + 1;
        period.set(nextPeriod);
    }
}
