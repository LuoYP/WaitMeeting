package org.example.schedule;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TimeWheel {
    private static final int DEFAULT_TICKS_PER_WHEEL = 60;
    private static final int DEFAULT_TICK_DURATION = 1;

    private final int ticksPerWheel;
    private final int tickDuration;
    private final DelayQueue<TimeWheelTaskList> delayQueue;
    private final long startTime;

    private volatile int currentTickIndex = 0;
    private final TimeWheelTaskList[] wheel;

    public TimeWheel() {
        this(DEFAULT_TICKS_PER_WHEEL, DEFAULT_TICK_DURATION);
    }

    public TimeWheel(int ticksPerWheel, int tickDuration) {
        this.ticksPerWheel = ticksPerWheel;
        this.tickDuration = tickDuration;
        this.delayQueue = new DelayQueue<>();
        this.startTime = System.currentTimeMillis();
        this.wheel = new TimeWheelTaskList[ticksPerWheel];
        for (int i = 0; i < wheel.length; i++) {
            wheel[i] = new TimeWheelTaskList();
        }
    }

    public void addTask(TimeWheelTask task) {
        long expirationTime = task.getExpirationTime();
        if (expirationTime < startTime) {
            return;
        }
        if (expirationTime < startTime + tickDuration) {
            wheel[currentTickIndex].addTask(task);
        } else {
            long delay = expirationTime - startTime;
            int ticks = (int) (delay / tickDuration);
            int stopIndex = currentTickIndex + ticks % ticksPerWheel;
            wheel[stopIndex].addTask(task);
        }
    }

    public void start() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    TimeWheelTaskList taskList = delayQueue.take();
                    taskList.flush(this::addTask);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.start();

        Thread tickThread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(tickDuration);
                    currentTickIndex = (currentTickIndex + 1) % ticksPerWheel;
                    wheel[currentTickIndex].flush(task -> {
                        TimeWheelTaskList taskList = new TimeWheelTaskList();
                        taskList.addTask(task);
                        delayQueue.offer(taskList);
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        tickThread.start();
    }
}

class TimeWheelTaskList implements Delayed{
    private TimeWheelTask head;
    private TimeWheelTask tail;

    public void addTask(TimeWheelTask task) {
        if (head == null) {
            head = tail = task;
        } else {
            tail.next = task;
            task.prev = tail;
            tail = task;
        }
    }

    public void flush(Consumer<TimeWheelTask> consumer) {
        TimeWheelTask task = head;
        while (task != null) {
            TimeWheelTask next = task.next;
            if (task.isCancelled()) {
                if (head == task && tail == task) {
                    head = tail = null;
                } else if (head == task) {
                    head = task.next;
                    head.prev = null;
                } else if (tail == task) {
                    tail = task.prev;
                    tail.next = null;
                } else {
                    task.prev.next = task.next;
                    task.next.prev = task.prev;
                }
            } else {
                if (task.getDelay(TimeUnit.MILLISECONDS) <= 0) {
                    consumer.accept(task);
                }
            }
            task = next;
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        if (head == null) {
            return Long.MAX_VALUE;
        }
        return unit.convert(head.getDelay(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o instanceof TimeWheelTaskList) {
            TimeWheelTaskList other = (TimeWheelTaskList) o;
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
        }
        return 0;
    }
}

class TimeWheelTask implements Delayed {
    private final long expirationTime;
    private Runnable task;
    TimeWheelTask prev;
    TimeWheelTask next;

    public TimeWheelTask(long expirationTime, Runnable task) {
        this.expirationTime = expirationTime;
        this.task = task;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isCancelled() {
        return task == null;
    }

    public void cancel() {
        task = null;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expirationTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }

    public void run() {
        if (task != null) {
            task.run();
        }
    }
}
