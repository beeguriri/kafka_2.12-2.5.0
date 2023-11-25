# kafka_2.12-2.5.0
아파치 카프카 애플리케이션 프로그래밍을 공부 합니다!

# 목차
1. [kafka 실행](#kafka-실행)
2.


# kafka 실행
### ✅ 실행 환경 : Ubuntu 20.04.3 LTS
### ✅ 자바 설치
```bash
$ sudo apt-get update
$ sudo apt-get install openjdk-11-jdk
```
### ✅ Kafka 다운로드    
```bash
# 다운로드
$ wget https://archive.apache.org/dist/kafka/2.5.0/kafka_2.12-2.5.0.tgz

# 압축해제
$ tar -xvzf kafka_2.12-2.5.0.tgz

# 확인
$ ls ~/kafka_2.12-2.5.0
LICENSE  NOTICE  bin  config  libs  site-docs

# 브로커에서 적재한 데이터 확인을 위한 폴더 생성
$ cd kafka_2.12-2.5.0/
$ mkdir data
$ ls
LICENSE  NOTICE  bin  config  data  libs  site-docs
```
    
### ✅ server.properties 수정
```bash
$ vi config/server.properties

# 수정된거 확인
$ cat config/server.properties| grep log.dirs
```

### ✅ Zookeeper 실행
```bash
$ bin/zookeeper-server-start.sh config/zookeeper.properties

```

### ✅ Kafka broker 실행
```bash
$ bin/kafka-server-start.sh config/server.properties
```

### ✅ 실행 확인
![](images/Execution.png)

```bash
# 브로커 확인
$ bin/kafka-broker-api-versions.sh --bootstrap-server localhost:9092
localhost:9092 (id: 0 rack: null) -> (
        Produce(0): 0 to 8 [usable: 8],
        Fetch(1): 0 to 11 [usable: 11],
        ListOffsets(2): 0 to 5 [usable: 5],
        Metadata(3): 0 to 9 [usable: 9],
...

# 토픽 확인
$ bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### ✅ 참고
```bash
# host에 local 추가해서 추후 편리하게 이용할 수 있도록
$ sudo vi /etc/hosts

127.0.0.1 my-kafka # <<추가!
```


