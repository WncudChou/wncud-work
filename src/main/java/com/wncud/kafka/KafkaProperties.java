package com.wncud.kafka;

/**
 * Created by yajunz on 2014/11/21.
 */
public interface KafkaProperties {
    //final static String zkConnect = "192.168.53.13:2181";
    final static String zkConnect = "192.168.8.236:2181";
    final static String groupId = "yajunz_test";
    final static String topic = "event_topic";
    final static String kafkaServiceURL = "192.168.69.9";
    final static int kafkaServicePort = 9092;
    final static int kafkaProducerBufferSize = 64 * 1014;
    final static int connectionTimeOut = 20000;
    final static int reconnectInterval = 10000;
    final static String clientId = "wncudConsumer";
}
