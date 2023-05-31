package org.example.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 时间轮管理定时任务
 */
public class TimeWheel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeWheel.class);

    //每轮默认刻度:60
    private static final int DEFAULT_TICKS_PER_WHEEL = 60;

    //每个刻度默认步长:1ms
    private static final int DEFAULT_TICK_DURATION = 1;

    //执行定时任务的线程池数量: 默认30(线程池还可优化)
    private static final int DEFAULT_WORK_THREADS = 30;

    private final int ticksPerWheel;

    private final int tickDuration;

    //槽,用来存放任务的环形数组
    private LinkedList<TimeWheelTask>[] timeWheelTaskLinks;

    //环形数组的指针
    private volatile int currentTickIndex = 0;

    private long startTime;

    private ExecutorService bossGroup;

    private ExecutorService workGroup;

    public TimeWheel() {
        this(DEFAULT_TICKS_PER_WHEEL, DEFAULT_TICK_DURATION, DEFAULT_WORK_THREADS);
    }

    public TimeWheel(int ticksPerWheel, int tickDuration, int workThreadNumbers) {
        this.ticksPerWheel = ticksPerWheel;
        this.tickDuration = tickDuration;
        this.startTime = System.currentTimeMillis();
        timeWheelTaskLinks = new LinkedList[ticksPerWheel];
        for (int i = 0; i < ticksPerWheel; i++) {
            timeWheelTaskLinks[i] = new LinkedList<>();
        }
        bossGroup = Executors.newFixedThreadPool(1);
        workGroup = Executors.newFixedThreadPool(workThreadNumbers);
    }

    public void start() {
        bossGroup.execute(() -> {
            while (true) {
                try {
                    //遍历当前槽是否有在当前周期中快要过期的任务
                    LinkedList<TimeWheelTask> timeWheelTaskLink = timeWheelTaskLinks[currentTickIndex];
                    Iterator<TimeWheelTask> iterator = timeWheelTaskLink.iterator();
                    while (iterator.hasNext()) {
                        TimeWheelTask task = iterator.next();
                        long scheduleTime = task.scheduleTime();
                        if (scheduleTime - System.currentTimeMillis() <= tickDuration) {
                            workGroup.submit(task.runnable());
                            iterator.remove();
                        }
                    }
                    currentTickIndex = (currentTickIndex + 1) % ticksPerWheel;
                    TimeUnit.MILLISECONDS.sleep(tickDuration);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });

    }

    public void stop() {
        bossGroup.shutdown();
        workGroup.shutdown();
    }

    public boolean addTask(TimeWheelTask timeWheelTask) {
        long scheduleTime = timeWheelTask.scheduleTime();
        if (scheduleTime < startTime) {
            //已过期
            return false;
        }
        //根据超时时间将任务分配到对应的槽
        if (scheduleTime < startTime + tickDuration) {
            timeWheelTaskLinks[currentTickIndex].add(timeWheelTask);
        } else {
            int slot = (int) (scheduleTime - startTime) / tickDuration;
            int taskIndex = slot % ticksPerWheel;
            timeWheelTaskLinks[taskIndex].add(timeWheelTask);
        }
        return true;
    }
}
