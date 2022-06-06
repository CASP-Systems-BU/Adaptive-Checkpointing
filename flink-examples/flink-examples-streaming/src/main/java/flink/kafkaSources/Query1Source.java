package flink.kafkaSources;

import flink.sources.BidSourceFunction;
import flink.utils.BidSchema;
import org.apache.beam.sdk.nexmark.model.Bid;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query1Source {
    public static void main(String[] args) throws Exception {
        System.out.println("Options for both the above setups: ");
        System.out.println("\t[--kafka-topic <topic>]");
        System.out.println("\t[--brokers <brokers>]");
        System.out.println("\t[--ratelist <ratelist>]");
        System.out.println("\t[--backend <hashmap|rocks>]");
        System.out.println("\t[--checkpoint-dir <filepath>]");
        System.out.println("\t[--incremental-checkpoints <true|false>]");
        System.out.println();

        // Checking input parameters
        //  --kafka-topic <topic>
        //  --broker <broker>
        //  --ratelist 250_300000_11000_300000
        final ParameterTool params = ParameterTool.fromArgs(args);
        String broker = params.getRequired("broker");
        String kafkaTopic = params.getRequired("kafka-topic");
        System.out.printf("Reading from kafka topic %s @ %s\n", kafkaTopic, broker);
        System.out.println();
        final String checkpointDir = params.get("checkpoint-dir");
        boolean incrementalCheckpoints = params.getBoolean("incremental-checkpoints", false);
        String ratelist = params.getRequired("ratelist");
        int[] numbers = Arrays.stream(ratelist.split("_")).mapToInt(Integer::parseInt).toArray();
        System.out.println(Arrays.toString(numbers));
        List<List<Integer>> rates = new ArrayList<>();
        for (int i = 0; i < numbers.length - 1; i += 2) {
            rates.add(Arrays.asList(numbers[i], numbers[i + 1]));
        }

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new EmbeddedRocksDBStateBackend(incrementalCheckpoints));
        env.getCheckpointConfig().setCheckpointStorage(checkpointDir)

        DataStream<Bid> bids =
                env.addSource(new BidSourceFunction(rates))
                        .setParallelism(params.getInt("p-source", 1))
                        .name("Bids Kafka Source")
                        .uid("Bids-Kafka-Source");

        // write the filtered data to a Kafka sink
        KafkaSink<Bid> sink =
                KafkaSink.<Bid>builder()
                        .setBootstrapServers(broker)
                        .setDeliverGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                        .setRecordSerializer(
                                KafkaRecordSerializationSchema.builder()
                                        .setTopic(kafkaTopic)
                                        .setValueSerializationSchema(new BidSchema())
                                        .build())
                        .build();

        bids.sinkTo(sink);
        // run the cleansing pipeline
        env.execute("Bid Events to Kafka");
    }

}
