import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

/**
 * 토픽 먼저 생성 후 해당 애플리케이션 실행
 * $  .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --topic stream_log --create
 * $  .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --topic stream_log_filter --create
 */

public class SimpleStreamFilterApplication {

    private final static String APPLICATION_NAME = "streams-filter-application"; //카프카 컨슈머 group.id 처럼 활용
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String STREAM_LOG = "stream_log"; //토픽 이름
    private final static String STREAM_LOG_FILTER = "stream_log_filter"; //토필 이름

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        //소스 데이터 가져옴
        KStream<String, String> streamLog = builder.stream(STREAM_LOG);

        //필터링 된 새로운 데이터 스트림을 만듬
        KStream<String, String> filteredStream = streamLog.filter((key, value) -> value.length() > 5);

        //특정 토픽의 데이터를 저장
        filteredStream.to(STREAM_LOG_FILTER);

        //kafka-streams 를 통해 실행
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}
