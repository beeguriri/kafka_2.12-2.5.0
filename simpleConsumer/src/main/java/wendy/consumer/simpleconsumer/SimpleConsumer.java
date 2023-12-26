package wendy.consumer.simpleconsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
/**
 * 22:24:41.202 [main] DEBUG org.apache.kafka.clients.consumer.internals.Fetcher -
 * [Consumer clientId=consumer-test-group-1, groupId=test-group]
 * Sending READ_UNCOMMITTED IncrementalFetchRequest(toSend=(), toForget=(), implied=(test-0)) to broker localhost:9092 (id: 0 rack: null)
 */
public class SimpleConsumer {
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(List.of(TOPIC_NAME));
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for(ConsumerRecord<String, String> record: records)
                log.info("record: {}", record);
        }

    }

}
