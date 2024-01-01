# 분산모드 카프카 커넥트 - REST API - 실습

### 💜 실행
```bash
# zookeeper 실행
$ .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# kafka 실행
$ .\bin\windows\kafka-server-start.bat .\config\server.properties

# 분산모드 properties 수정
# kafka_2.13-2.5.0\config\connect-distributed.properties
>> bootstrap.servers=localhost:9092
>> key.converter=org.apache.kafka.connect.storage.StringConverter
>> value.converter=org.apache.kafka.connect.storage.StringConverter
>> key.converter.schemas.enable=false
>> value.converter.schemas.enable=false
>> offset.flush.interval.ms=10000

# 분산모드 실행
$ .\bin\windows\connect-distributed.bat .\config\connect-distributed.properties

# API test
$ curl -X GET http://localhost:8083/connector-plugins
[{"class":"org.apache.kafka.connect.file.FileStreamSinkConnector","type":"sink","version":"2.5.0"},{"class":"org.apache.kafka.connect.file.FileStreamSourceConnector","type":"source","version":"2.5.0"},{"class":"org.apache.kafka.connect.mirror.MirrorCheckpointConnector","type":"source","version":"1"},{"class":"org.apache.kafka.connect.mirror.MirrorHeartbeatConnector","type":"source","version":"1"},{"class":"org.apache.kafka.connect.mirror.MirrorSourceConnector","type":"source","version":"1"}]
```

### 💜 커넥터 만들기
```bash
$ curl -X POST http://localhost:8083/connectors \
> -H 'Content-Type: application/json'
> -d ' {"name":"file-sink-test", \
        "config":{
            "topics":"test",
            "connector.class":"org.apache.kafka.connect.file.FileStreamSinkConnector", "tasks.max":1, "file":"/tmp/connect-test-data.txt"
        }
    }'

# 만들어 진거 확인
$ curl -X GET http://localhost:8083/connectors
["file-sink-test"]

# 상태 확인
$ curl -X GET http://localhost:8083/connectors/file-sink-test/status
{"name":"file-sink-test","connector":{"state":"RUNNING","worker_id":"192.168.0.111:8083"},"tasks":[{"id":0,"state":"RUNNING","worker_id":"192.168.0.111:8083"}],"type":"sink"}
```

### 💜 데이터 넣고 파일로 생성됨을 확인
- 이전에 test 브로커에 작성 된 메시지까지 확인 됨
![](/images/connector_test.png)

### 💜 커넥터 종료
```bash
$ curl -X DELETE http://localhost:8083/connectors/file-sink-test
$ curl -X GET http://localhost:8083/connectors
[]
```

