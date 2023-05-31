package org.example.schedule;

public class TimeWheelTask {

    private long scheduleTime;

    private Runnable runnable;

    public TimeWheelTask(long scheduleTime, Runnable runnable) {
        this.scheduleTime = scheduleTime;
        this.runnable = runnable;
    }

    public long scheduleTime() {
        return scheduleTime;
    }

    public Runnable runnable() {
        return runnable;
    }
}
