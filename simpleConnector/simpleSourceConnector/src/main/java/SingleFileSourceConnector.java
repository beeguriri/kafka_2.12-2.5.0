import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 태스크를 실행하기 전 커넥터 설정파일을 초기화하고,
 * 어떤 태스크 클래스를 사용할 것인지 정의
 */

public class SingleFileSourceConnector extends Connector {

    private final Logger logger = LoggerFactory.getLogger(SingleFileSourceConnector.class);

    private Map<String, String> configProperties;

    //필요한 옵션값 초기화
    @Override
    public void start(Map<String, String> props) {
        this.configProperties = props;
        try {
            new SingleFileSourceConfig(props);
        } catch (ConfigException e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    //실행하고자하는 태스크 클래스
    //일반적으로는 커넥터 하나 당 태스크 하나
    @Override
    public Class<? extends Task> taskClass() {
        return SingleFileSourceTask.class;
    }

    //태스크마다 다른 옵션 주고싶을때
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>(configProperties);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    //커넥터 리소스 해제
    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return SingleFileSourceConfig.CONFIG;
    }

    @Override
    public String version() {
        return "1.0";
    }
}
