package com.wncud.kafka;

import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yajunz on 2014/12/23.
 */
public class KafkaTest {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", KafkaProperties.zkConnect);
        properties.put("group.id", "test_11");
        properties.put("zookeeper.session.timeout.ms", "400");
        properties.put("auto.commit.enable", "false");
        //properties.put("auto.offset.reset", "smallest");
        properties.put("auto.offset.reset", "largest");
        properties.put("consumer.timeout.ms", "5000");
        //properties.put("client.id", "yajunz_test2_name");

        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));

        String topic = "zhouyajun_test";

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
        try {
            while (true){
                try {
                    while (iterator.hasNext()){
                        MessageAndMetadata<byte[], byte[]> metadata = iterator.next();
                    /*String message = new String(metadata.message());*/
                        //System.out.println(message);
                        System.out.println("receive:" + "[offset-" + metadata.offset() + "|p-" + metadata.partition() + "] > " /*+message*/);
                        consumer.commitOffsets();
                    }
                } catch (ConsumerTimeoutException e){
                    System.out.println("################time out!!!##################");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        consumer.shutdown();
    }

    @Test
    public void testSend(){
        Producer producer = new Producer("yajunz_test_2");
        producer.start();
    }
}
