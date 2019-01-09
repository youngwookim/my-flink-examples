package com.example;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class FlinkStreamingKafkaJob {

	public static void main(String[] args) throws Exception {
		// parse user parameters
		ParameterTool parameterTool = ParameterTool.fromArgs(args);

		if (parameterTool.getNumberOfParameters() < 1) {
			System.out.println("Missing parameters!\n" + "Usage: --bootstrap.servers <kafka brokers> --input-topic <topic> ");
			return;
		}
		Properties config = new Properties();
		config.setProperty("bootstrap.servers", parameterTool.getRequired("bootstrap.servers"));

		// create execution environment
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// FIXME
		DataStream<String> input = env.addSource(new FlinkKafkaConsumer<>(parameterTool.getRequired("input-topic"),
				new SimpleStringSchema(), config));

		input.map(new MapFunction<String, String>() {
			private static final long serialVersionUID = -6867736771747690202L;

			@Override
			public String map(String value) throws Exception {
				return "Kafka and Flink says: " + value;
			}

		}).print();

		env.execute("Flink Kafka Example");
	}
}
