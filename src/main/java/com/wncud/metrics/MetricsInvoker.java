package com.wncud.metrics;

import com.codahale.metrics.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyajun on 2016/1/6.
 */
public class MetricsInvoker {
    private final static MetricRegistry metrics = new MetricRegistry();
    //private static MetricRegistry metrics;

    private static Queue<String> queue = new LinkedBlockingDeque<>();

    private Random random = new Random();

    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();

    //private static Counter countJobs = metrics.counter(MetricRegistry.name(MetricsInvoker.class, "counter-jobs"));
    //private static Counter countJobs = null;
    //private static final Meter meterJobs = null;
    //private static final Meter meterJobs = metrics.meter(MetricRegistry.name(MetricsInvoker.class, "meter-jobs"));

    @Before
    public void init(){
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-beans.xml");
        //metrics = context.getBean("metricRegistry", MetricRegistry.class);
    }

    @Test
    public void testGauge() throws InterruptedException {
        //reporter.start(3, TimeUnit.SECONDS);

        Gauge<Integer> gauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return queue.size();
            }
        };

        metrics.register(MetricRegistry.name(MetricsInvoker.class, "pending-jog"), gauge);

        for (int i=0; i<50000; i++){
            queue.add("a");
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCounter(){
        //reporter.start(3, TimeUnit.SECONDS);
        Counter countJobs = metrics.counter(MetricRegistry.name(MetricsInvoker.class, "count-job"));
        for(int i = 0; i < 50000; i++){
            countJobs.inc();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMeter(){
        reporter.start(3, TimeUnit.SECONDS);
        Meter meterJobs = metrics.meter(MetricRegistry.name(MetricsInvoker.class, "meter-jobs"));
        for(int i = 0; i < 50000; i++){
            meterJobs.mark(random.nextInt(500000));
            try {
                //Thread.sleep(random.nextInt(2000));
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testTimer(){
        Timer timerJobs = metrics.timer(MetricRegistry.name(MetricsInvoker.class, "timer-jobs"));
        Timer.Context context;
        for(int i = 0; i < 50000; i++){
            context = timerJobs.time();
            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.stop();
        }
    }
}
