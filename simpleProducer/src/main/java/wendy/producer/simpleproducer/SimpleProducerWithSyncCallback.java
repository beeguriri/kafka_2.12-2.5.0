package wendy.producer.simpleproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class SimpleProducerWithSyncCallback {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {

        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //default 1 => 전송한 메시지 받으면 응답 함.
        // 0 => 전송한 메시지에 대한 응답 없음.
//        configs.put(ProducerConfig.ACKS_CONFIG, "0");

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        ProducerRecord<String, String> record1 = new ProducerRecord<>(TOPIC_NAME, "key", "message");

        try{
            RecordMetadata recordMetadata = producer.send(record1).get();
            log.info("recordMetadata1 : {}", recordMetadata.toString());
        } catch (Exception e){
            log.error(e.getMessage());
        } finally {
            producer.flush();
            producer.close();
        }

        /* > akcs=1
        * recordMetadata: test-0@7
        * 0번 파티션에 offset 7번에 저장 됨
        *
        * > akcs=0
        * recordMetadata: test-0@-1
        * 0번 파티션에 offset -1 < = 없는 값
        */
    }
}
