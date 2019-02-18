package com.bizdirect.orders.proto.messages;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class ConsumerOrderRelated extends  BaseTest
{

    static Utils utils = new Utils();

    static String status;

    private static final Logger logger = Logger.getLogger(ConsumerOrderRelated.class.getName());


    @Test
    public void MessageOfPaymentToOreder()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.16.0.32:9092");
        props.put("group.id", "test");
        // props.put("enable.auto.commit", "true");
        //props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("paymentToOrderTopic"));


        try
        {
            while (true)
            {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records)
                {
                     System.out.println(record.value());


                }
            }

        } catch(Exception e) {
            //  LOGGER.error("Exception occured while consuing messages",e);
        }finally {
            consumer.close();
        }


    }
}
