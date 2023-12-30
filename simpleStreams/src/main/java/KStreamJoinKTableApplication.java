import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

/**
 * 토픽 3개 먼저 생성 후 해당 애플리케이션 실행
 * KStream, KTable을 join 할때는 파티션 개수를 동일하게 해야함.
 * Kafka topic을 만들때 KStream, KTable 사용여부를 지정하지 않음.
 * => `kafka streams의 몫!!`
 * $ .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --create --partitions 3 --topic address
 * $ .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --create --partitions 3 --topic order
 * $ .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --create --partitions 3 --topic order_join
 */

public class KStreamJoinKTableApplication {

    private final static String APPLICATION_NAME = "order-join-application"; //카프카 컨슈머 group.id 처럼 활용
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String ADDRESS_TABLE = "address";
    private final static String ORDER_STREAM = "order";
    private final static String ORDER_JOIN_STREAM = "order_join";

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //조인을 위해 두개의 소스프로세스 필요
        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE); //key의 최신 데이터만 사용
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM); //key의 모든 데이터 사용

        //조인 -> 별도의 조인 키를 지정 해 주지 않음
        //메시지키가 같으면 무조건 조인을 수행 함
        orderStream
                .join(addressTable, (order, address) -> order + " send to " + address)
                //to 메서드로 오더 조인한 스트림을 생성
                .to(ORDER_JOIN_STREAM);

        //kafka-streams 를 통해 실행
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}
