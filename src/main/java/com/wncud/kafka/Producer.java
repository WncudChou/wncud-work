package com.wncud.kafka;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by yajunz on 2014/11/21.
 */
public class Producer extends Thread{

    private final kafka.javaapi.producer.Producer<Integer, String> producer;
    private final String topic;
    private final Properties properties = new Properties();

    public Producer(String topic) {
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("metadata.broker.list", "192.168.69.40:9092");
        producer = new kafka.javaapi.producer.Producer<Integer, String>(new ProducerConfig(properties));
        this.topic = topic;
    }

    @Override
    public void run() {
        int messageNo = 1;
        int count = 0;
        while (count < 1000){
            try {
                String messageStr = new String("message_" + messageNo);
                producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
                messageNo++;
                count++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        producer.close();
    }

    @Override
    protected void finalize() throws Throwable {
        if(producer != null){
            try {
                producer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.finalize();
    }

    public static void main(String[] args) {
        Producer send = new Producer("yajunz_test_2");
        send.start();
    }
}
