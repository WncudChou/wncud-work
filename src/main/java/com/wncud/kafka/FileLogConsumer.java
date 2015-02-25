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
public class FileLogConsumer{
    private final ConsumerConnector consumer;
    private final String topic;

    public FileLogConsumer(String topic) {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", KafkaProperties.zkConnect);
        properties.put("group.id", KafkaProperties.groupId);
        properties.put("zookeeper.session.timeout.ms", "400");
        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
        this.topic = topic;
    }

    public void start() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        try {
            ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
            while (iterator.hasNext()){
                MessageAndMetadata<byte[], byte[]> metadata = iterator.next();
                //String message = new String(metadata.message());
                System.out.println("receive:" + "[offset-" + metadata.offset() + "|p-" + metadata.partition() + "] > " /*+message*/);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        consumer.shutdown();
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
