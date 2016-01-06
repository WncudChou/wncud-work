package com.wncud.influxdb;

import com.google.common.base.Stopwatch;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by zhouyajun on 2015/9/11.
 */
public class InfluxdbClient {

    private InfluxDB influxDB;
    private String dbName;

    private final static int COUNT = 10;
    private final static int POINT_COUNT = 5;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Before
    public void init(){
        String url = "http:119.254.97.34:8082";
        influxDB = InfluxDBFactory.connect(url, "root", "root");
        dbName =  "monitorTestDb";
    }

    @Test
    public void testQuery(){
        //Query query = new Query("select sum(\"offset\"), sum(logSize) from monitor_partition where topic='order_create_wm' group by time", dbName);
        //Query query = new Query("select last(\"offset\"),last(\"logSize\"), last(lag) from monitor_partition where topic='order_create_wm' and time > now() - 20h group by time(1h) limit 60", dbName);
        Query query = new Query("select last(\"offset\") as \"offset\", last(logSize) as logSize, last(lag) as lag from monitor_topic where consumerGroup='group_order_ledger' and topic='order_create_wm' and time >= '2015-09-21 12:00:01' group by time(1h)", dbName);
        QueryResult result = influxDB.query(query);
        System.out.println(result.toString());
    }

    @Test
    public void testDelete(){
        influxDB.deleteDatabase(dbName);
    }

    @Test
    public void createDate(){
        influxDB.createDatabase(dbName);

        Stopwatch watch = Stopwatch.createStarted();
        Random random = new Random(System.currentTimeMillis());
        int[] offsets = new int[]{1, 1, 1, 1, 1};
        for (int i = COUNT; i >0 ; i--) {

            BatchPoints batchPoints = BatchPoints
                    .database(dbName)
                    .retentionPolicy("default")
                    .build();
            long time = System.currentTimeMillis();
            time = time - (1000*60*60*i);
            for (int j = 0; j < POINT_COUNT; j++) {
                offsets[j] = offsets[j] + random.nextInt(100);
                Point point = Point
                        .measurement("p_monitor")
                        .tag("partition", "partition_" + j)
                        .time(time, TimeUnit.MILLISECONDS)
                        .field("offset", offsets[j])
                        .build();
                batchPoints.point(point);
            }

            this.influxDB.write(batchPoints);
        }
        System.out.println("WritePoints for " + COUNT + " writes of " + POINT_COUNT + " Points took:" + watch);
    }

    @Test
    public void testPing(){
        boolean influxDBstarted = false;
        String url = "http://119.254.97.34:8082";
        InfluxDB influxDB = InfluxDBFactory.connect(url, "root", "root");
        do {
            Pong response;
            try {
                response = influxDB.ping();
                System.out.println(response);
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                // NOOP intentional
                e.printStackTrace();
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!influxDBstarted);
        influxDB.setLogLevel(InfluxDB.LogLevel.FULL);
        // String logs = CharStreams.toString(new InputStreamReader(containerLogsStream,
        // Charsets.UTF_8));
        System.out.println("##################################################################################");
        // System.out.println("Container Logs: \n" + logs);
        System.out.println("#  Connected to InfluxDB Version: " + influxDB.version() + " #");
        System.out.println("##################################################################################");
    }
}
