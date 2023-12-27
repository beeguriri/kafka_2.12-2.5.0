package com.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String GROUP_ID = "test-group";
    private static KafkaConsumer<String, String> consumer;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); //default가 true임
        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 60000); //interval 시간 마다 커밋 수행

        consumer = new KafkaConsumer<>(configs);

        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        // 보통 무한루프 사용
        // 안정적으로 컨슈머 애플리캐이션을 종료하기 위하여 wakeup() 메서드 사용
        // 정상적으로 종료되지 않은 컨슈머는 세션 타임아웃이 발생할 때 까지
        // 컨슈머 그룹에 남게되고, 데이터의 유실 발생할 수 있음
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("record:{}", record);
                }
            }
        } catch (WakeupException e){
            logger.warn("Wakeup consumer");
            /*
            윈도우 파워쉘에서는 어떻게 종료해야할지 모르겠다
            리눅스는 ps -ef | grep SimpleConsumer 로 PID 확인 후 kill -term [PID]
            윈도우는 jps로 PID 확인 후 종료는 Stop-Process -Id [PID]
            근데 여기서 강제종료가 되어버려서... 프로세스 종료할 시간을 안줘서 캐치문으로 안가니?
           */
        } finally {
            consumer.close();
        }

    }

    static class ShutdownThread extends Thread {
        @Override
        public void run() {
            logger.info("shutdown hook");
            consumer.wakeup();
        }
    }
}