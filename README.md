# Flink  Examples

## Prerequisites

* Flink 1.7.0+
* Kafka 1.0.0+
* Docker & Docker Compose

## How to create a Flink project

```
$ mvn archetype:generate                               \
      -DarchetypeGroupId=org.apache.flink              \
      -DarchetypeArtifactId=flink-quickstart-java      \
      -DarchetypeVersion=1.7.1
      
......


$ cd project-name
......

$ mvn clean package

```

## How to run examples on Docker

```
$ cd docker
$ docker-compose up
```

```
# Kafka

$ KAFKA_BROKER=$(docker ps --filter name=kafka1 --format={{.ID}})
$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --create --topic foo --partitions 4 --replication-factor 1 \
--if-not-exists --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --create --topic hello --partitions 4 --replication-factor 1 \
--if-not-exists --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --create --topic world --partitions 4 --replication-factor 1 \
--if-not-exists --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --describe --topic foo --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --describe --topic hello --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-topics --describe --topic world --zookeeper zoo1:2181

$ docker exec -t -i "$KAFKA_BROKER" \
bash -c "seq 100 | kafka-console-producer --request-required-acks 1 \
--broker-list kafka1:19092 --topic foo && echo 'Produced 100 messages.'"

$ docker exec -t -i "$KAFKA_BROKER" \
kafka-console-consumer --bootstrap-server kafka1:19092 --topic foo --from-beginning --max-messages 100

# Flink

# Flink - Kafka Streaming job (basic)
$ JOBMANAGER_CONTAINER=$(docker ps --filter name=jobmanager --format={{.ID}})
$ docker cp flink-kafka/target/flink-kafka-1.0-SNAPSHOT.jar "$JOBMANAGER_CONTAINER":/flink-kafka-1.0-SNAPSHOT.jar
$ docker exec -t -i "$JOBMANAGER_CONTAINER" flink run /flink-kafka-1.0-SNAPSHOT.jar \
--bootstrap.servers kafka1:19092 \
--input-topic foo 

# Generate Kafka Events
$ docker exec -t -i "$KAFKA_BROKER" \
bash -c "seq 100 | kafka-console-producer --request-required-acks 1 \
--broker-list kafka1:19092 --topic foo && echo 'Produced 100 messages.'"


# Flink - Kafka Streaming Job (Schema Registry)

# Produce avro events to Kafka
$ mvn clean package
$ cd kafka-client-avro
$ mvn exec:java -f pom.xml -Dexec.mainClass=com.example.kafka.KafkaProducerAvro
......

$ cd -
```

```
$ JOBMANAGER_CONTAINER=$(docker ps --filter name=jobmanager --format={{.ID}})
$ docker cp flink-kafka-schema-registry/target/flink-kafka-schema-registry-1.0-SNAPSHOT.jar "$JOBMANAGER_CONTAINER":/flink-kafka-schema-registry-1.0-SNAPSHOT.jar
$ docker exec -t -i "$JOBMANAGER_CONTAINER" flink run /flink-kafka-schema-registry-1.0-SNAPSHOT.jar \
--input-topic hello \
--output-topic world \
--bootstrap.servers kafka1:19092 \
--schema-registry-url http://kafka-schema-registry:8089/ \
--group.id cgrp1

$ KAFKA_BROKER=$(docker ps --filter name=kafka1 --format={{.ID}})
$ docker exec -t -i "$KAFKA_BROKER" \
kafka-console-consumer --bootstrap-server kafka1:19092 --topic world --from-beginning --max-messages 100

```

## Web UI

* Flink Job Manager: http://localhost:8081
* Kafka Schema Registry UI: http://localhost:8001

## Refs.

* https://github.com/simplesteph/kafka-stack-docker-compose
* https://docs.confluent.io/current/installation/docker/docs/installation/single-node-client.html
* https://ci.apache.org/projects/flink/flink-docs-release-1.7/ops/deployment/docker.html
* https://hub.docker.com/_/flink
* https://github.com/dataArtisans/kafka-example
* https://github.com/apache/flink/tree/master/flink-end-to-end-tests/flink-confluent-schema-registry
* https://github.com/simplesteph/kafka-stack-docker-compose

