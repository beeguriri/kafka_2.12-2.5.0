import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

/**
 * 토픽 3개 먼저 생성 후 해당 애플리케이션 실행
 * KStream, GlobalKTable을 join 할때는 파티션 개수 상관없이 조인할 수 있음.
 * 대신 조인할 키를 지정 해 줘야함.
 * $ .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --create --partitions 2 --topic address_v2
 */

public class KStreamJoinGlobalKTableApplication {

    private final static String APPLICATION_NAME = "order-join-application"; //카프카 컨슈머 group.id 처럼 활용
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String ADDRESS_GLOBAL_TABLE = "address_v2";
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
        GlobalKTable<String, String> addressTable = builder.globalTable(ADDRESS_GLOBAL_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        //조인
        orderStream
                .join(addressTable,
                        //파티션의 개수가 다른 globalKTable과 KStream 조인 시 키 지정 필요
                        (orderKey, orderValue) -> orderKey,
                        (order, address) -> order + " send to " + address)
                //to 메서드로 오더 조인한 스트림을 생성
                .to(ORDER_JOIN_STREAM);

        //kafka-streams 를 통해 실행
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}
