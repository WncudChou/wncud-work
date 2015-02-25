package com.wncud.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yajunz on 2014/11/21.
 */
public class KafkaConsumer extends Thread {
    private final ConsumerConnector consumer;
    private final String topic;

    public KafkaConsumer(String topic) {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", KafkaProperties.zkConnect);
        properties.put("group.id", KafkaProperties.groupId);
        properties.put("zookeeper.session.timeout.ms", "40000");
        properties.put("zookeeper.sync.time.ms", "200");
        properties.put("auto.commit.interval.ms", "1000");
        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
            System.out.println("xxx");
            topicCountMap.put(topic, new Integer(1));
            System.out.println("xxx");
            Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
            KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
            ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
            System.out.println("xxx");
            while (iterator.hasNext()){
                MessageAndMetadata<byte[], byte[]> metadata = iterator.next();
                //String message = new String(metadata.message());
                System.out.println("receive:" + "[offset-" + metadata.offset() + "|p-" + metadata.partition() + "] > " /*+message*/);
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            consumer.shutdown();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(consumer != null){
            try {
                consumer.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.finalize();
    }
}
