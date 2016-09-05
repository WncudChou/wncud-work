package com.wncud.thread;

import java.util.concurrent.*;

/**
 * Created by zhouyajun on 2016/4/5.
 */
public class ScheduleThreadExecutorTest {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void beepForAnHour(){
        final Runnable beeper = new Runnable() {
            @Override
            public void run() {
                System.out.println("beep");
            }
        };

        final ScheduledFuture<?> beeperHandle = scheduledExecutorService.scheduleAtFixedRate(beeper, 1, 1, TimeUnit.SECONDS);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                beeperHandle.cancel(true);
                System.out.println("over !!");
            }
        }, 30, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        ScheduleThreadExecutorTest executorTest = new ScheduleThreadExecutorTest();
        executorTest.beepForAnHour();

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
