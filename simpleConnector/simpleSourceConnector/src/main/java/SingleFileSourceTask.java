import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 실제로 데이터를 다루는 클래스
 * 소스 애플리케이션 또는 소스 파일로부터 데이터를 가져와서 토픽으로 데이터를 보내는 역할 수행
 * 토픽의 오프셋과 별개로 자체적으로 오프셋을 사용
 * 데이터를 중복으로 토픽에 보내는 것을 방지
 */

public class SingleFileSourceTask extends SourceTask {

    private Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);

    public final String FILENAME_FIELD = "filename";
    public final String POSITION_FIELD = "position";

    private Map<String, String> fileNamePartition;
    private Map<String, Object> offset;
    private String topic;
    private String file;
    private long position = -1;

    @Override
    public String version() {
        return "1.0";
    }

    //리소스 초기화
    @Override
    public void start(Map<String, String> props) {
        try {
            // Init variables
            SingleFileSourceConfig config = new SingleFileSourceConfig(props);
            topic = config.getString(SingleFileSourceConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConfig.DIR_FILE_NAME);
            fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);
            //소스 커넥터에서 관리하는 내부 offset 기록
            offset = context.offsetStorageReader().offset(fileNamePartition);

            // Get file offset from offsetStorageReader
            if (offset != null) {
                Object lastReadFileOffset = offset.get(POSITION_FIELD);
                if (lastReadFileOffset != null) {
                    position = (Long) lastReadFileOffset;
                }
            } else {
                position = 0;
            }

        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    // 프로듀싱을 위해 지속적으로 호출되는 메서드
    // 리턴하는 List가 send메서드처럼 토픽으로 보내짐
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> results = new ArrayList<>();
        try {
            Thread.sleep(1000);

            List<String> lines = getLines(position);

            if (!lines.isEmpty()) {
                lines.forEach(line -> {
                    Map<String, Long> sourceOffset = Collections.singletonMap(POSITION_FIELD, ++position);
                    SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic, Schema.STRING_SCHEMA, line);
                    results.add(sourceRecord);
                });
            }
            return results;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage(), e);
        }
    }

    private List<String> getLines(long readLine) throws Exception {
        BufferedReader reader = Files.newBufferedReader(Paths.get(file));
        return reader.lines().skip(readLine).collect(Collectors.toList());
    }

    //리소스 해제
    @Override
    public void stop() {

    }
}
