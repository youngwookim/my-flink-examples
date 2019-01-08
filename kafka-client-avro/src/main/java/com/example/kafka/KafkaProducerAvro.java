package com.example.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.example.avro.EventType;
import com.example.avro.User;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class KafkaProducerAvro {
	private static final String TOPIC = "hello";

	public static void main(String[] args) {
		final Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8089");

		try (KafkaProducer<String, User> producer = new KafkaProducer<String, User>(props)) {

			for (long i = 0; i < 100; i++) {
				final String name = "id" + Long.toString(i);
				final String favoriteNumber = Long.toString(i);
				final String favoriteColor = "color" + Long.toString(i);
				final User user = new User(name, favoriteNumber, favoriteColor, EventType.meeting);
				final ProducerRecord<String, User> record = new ProducerRecord<String, User>(TOPIC,
						user.getName().toString(), user);
				producer.send(record);
				Thread.sleep(1000L);
			}

		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		System.out.printf("Successfully produced 100 messages to a topic called %s%n", TOPIC);
	}

}
