package com.wncud.influxdb;

import com.google.common.base.Stopwatch;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyajun on 2015/9/15.
 */
public class PerformanceTests {
    private InfluxDB influxDB;
    private final static int COUNT = 1;
    private final static int POINT_COUNT = 100000;
    private final static int SINGLE_POINT_COUNT = 10000;

    @Before
    public  void setUp() {
        String url = "http:119.254.97.34:8082";
        this.influxDB = InfluxDBFactory.connect(url, "root", "root");
        this.influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
    }

    @Test
    public void writeSinglePointPerformance() throws InterruptedException {
        String dbName = "write_test";
        this.influxDB.createDatabase(dbName);
        this.influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
        Stopwatch watch = Stopwatch.createStarted();
        for (int j = 0; j < SINGLE_POINT_COUNT; j++) {
            Point point = Point.measurement("cpu").field("idle", j).field("user", 2 * j).field("system", 3 * j).build();
            this.influxDB.write(dbName, "default", point);
        }
        this.influxDB.disableBatch();
        System.out.println("Single Point Write for " + SINGLE_POINT_COUNT + " writes of  Points took:" + watch);
        //this.influxDB.deleteDatabase(dbName);
    }

    @Test
    public void writePerformance() {
        String dbName = "write_test_o";
        this.influxDB.createDatabase(dbName);

        Stopwatch watch = Stopwatch.createStarted();
        for (int i = 0; i < COUNT; i++) {

            BatchPoints batchPoints = BatchPoints
                    .database(dbName)
                    .tag("partition", "" + i)
                    .retentionPolicy("default")
                    .build();
            for (int j = 0; j < POINT_COUNT; j++) {
                Point point = Point
                        .measurement("cpu")
                        .field("idle", j)
                        .field("user", 2 * j)
                        .field("system", 3 * j)
                        .build();
                batchPoints.point(point);
            }

            this.influxDB.write(batchPoints);
        }
        System.out.println("WritePoints for " + COUNT + " writes of " + POINT_COUNT + " Points took:" + watch);
        //this.influxDB.deleteDatabase(dbName);
    }

    @Test
    public void maxWritePointsPerformance() {
        String dbName = "d";
        this.influxDB.createDatabase(dbName);
        this.influxDB.enableBatch(100000, 60, TimeUnit.SECONDS);

        Stopwatch watch = Stopwatch.createStarted();
        for (int i = 0; i < 2000000; i++) {
            Point point = Point.measurement("s").field("v", 1).build();
            this.influxDB.write(dbName, "default", point);
        }
        System.out.println("5Mio points:" + watch);
        this.influxDB.deleteDatabase(dbName);
    }

    @Test
    public void testWriteTopic(){
        String dbName = "topicDb";
        this.influxDB.createDatabase(dbName);
        this.influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
        Point point;

        for(int i = 1; i < 3; i++){
            Long time = System.currentTimeMillis();
            for(int j = 1; j < 6; j++){
                point = Point.measurement("producerTopic")
                        .tag("topic", "mytopic" +i)
                        .time(time, TimeUnit.MILLISECONDS)
                        .field("index", j)
                        .field("logsize", j).build();
                influxDB.write(dbName, "default", point);
            }
        }
        this.influxDB.disableBatch();
    }
}
