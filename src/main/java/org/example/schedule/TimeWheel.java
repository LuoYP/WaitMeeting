package org.example.schedule;

/**
 * 时间轮管理定时任务
 */
public class TimeWheel {

    //每轮默认刻度:60
    private static final int DEFAULT_TICKS_PER_WHEEL = 60;

    //每个刻度默认步长:1s
    private static final int DEFAULT_TICK_DURATION = 1;

    private final int ticksPerWheel;

    private final int tickDuration;

    //槽,用来存放任务的环形数组
    private TimeWheelTaskList[] timeWheelTaskLists;

    //环形数组的指针
    private volatile int currentTickIndex = 0;

    private long startTime;

    public TimeWheel() {
        this(DEFAULT_TICKS_PER_WHEEL, DEFAULT_TICK_DURATION);
    }

    public TimeWheel(int ticksPerWheel, int tickDuration) {
        this.ticksPerWheel = ticksPerWheel;
        this.tickDuration = tickDuration;
        this.startTime = System.currentTimeMillis();
        timeWheelTaskLists = new TimeWheelTaskList[ticksPerWheel];
        for (int i = 0; i < ticksPerWheel; i++) {
            timeWheelTaskLists[0] = new TimeWheelTaskList();
        }
    }

    public void start() {

    }

    public void addTask() {

    }
}
