package com.example;

import java.util.Properties;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.example.avro.User;

public class FlinkStreamingSchemaRegistryJob {

	public static void main(String[] args) throws Exception {
		// parse input arguments
		final ParameterTool parameterTool = ParameterTool.fromArgs(args);

		if (parameterTool.getNumberOfParameters() < 5) {
			System.out.println("Missing parameters!\n" + "Usage: Kafka --input-topic <topic> --output-topic <topic> "
					+ "--bootstrap.servers <kafka brokers> "
					+ "--schema-registry-url <confluent schema registry> --group.id <some id>");
			return;
		}
		Properties config = new Properties();
		config.setProperty("bootstrap.servers", parameterTool.getRequired("bootstrap.servers"));
		config.setProperty("group.id", parameterTool.getRequired("group.id"));
		String schemaRegistryUrl = parameterTool.getRequired("schema-registry-url");

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.getConfig().disableSysoutLogging();

		DataStreamSource<User> input = env.addSource(new FlinkKafkaConsumer<>(parameterTool.getRequired("input-topic"),
				ConfluentRegistryAvroDeserializationSchema.forSpecific(User.class, schemaRegistryUrl), config)
						.setStartFromEarliest());


		//SingleOutputStreamOperator<String> mapToString = input
		//.map((MapFunction<User, String>) SpecificRecordBase::toString);
		 
		DataStream<String> mapToString = input.map((MapFunction<User, String>) SpecificRecordBase::toString);

		mapToString.print();

		FlinkKafkaProducer<String> stringFlinkKafkaProducer = new FlinkKafkaProducer<>(
				parameterTool.getRequired("output-topic"), new SimpleStringSchema(), config);

		mapToString.addSink(stringFlinkKafkaProducer);

		env.execute("Kafka 1.0.0+ Confluent Schema Registry AVRO Example");
	}
}
