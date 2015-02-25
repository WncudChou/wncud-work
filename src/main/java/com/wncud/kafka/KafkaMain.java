package com.wncud.kafka;

import org.junit.Test;

/**
 * Created by yajunz on 2014/11/21.
 */
public class KafkaMain {
    public static void main(String[] args) {
        /*Producer producer = new Producer(KafkaProperties.topic);
        producer.start();
        KafkaConsumer consumer = new KafkaConsumer(KafkaProperties.topic);
        consumer.start();*/

        FileLogConsumer consumer = new FileLogConsumer("jun_test122219");
        consumer.start();
    }

    @Test
    public void testStartConsumer(){
        KafkaConsumer consumer = new KafkaConsumer("jun_test122224");
        consumer.start();
    }

    @Test
    public void testStartFileLogConsumer(){
        FileLogConsumer consumer = new FileLogConsumer("test_201412236");
        consumer.start();
    }
}
