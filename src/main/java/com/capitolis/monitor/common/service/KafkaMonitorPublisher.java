package com.capitolis.monitor.common.service;


import static com.capitolis.monitor.common.util.ProducerPropsLoader.loadProperties;

import com.capitolis.monitor.common.model.MonitorMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaMonitorPublisher implements MonitorResultPublisher {

    @Value("${capitolis.monitor.out.kafka.topic}")
    private String kafkaMonitorTopicName;

    private final Producer<String, MonitorMessage> producer;

    public KafkaMonitorPublisher(Producer<String, MonitorMessage> producer) {
        this.producer = producer;
    }


    @Override
    public boolean publish(MonitorMessage monitorMessage) {
        boolean isMessageSent = true;
        try {
            log.info("Publishing monitor result for request id {} to kafka on topic {}, spanName {}", monitorMessage.getTraceId(), kafkaMonitorTopicName, monitorMessage.getSpanName());
            producer.send(new ProducerRecord<>(kafkaMonitorTopicName, monitorMessage));
            log.info("Published monitor message {}", monitorMessage, monitorMessage.getSpanName());
        } catch(Exception ex) {
            log.error("Failed to send monitor message {}", monitorMessage, ex);
            isMessageSent = false;
        }

        return isMessageSent;
    }

    @Bean
    protected static Producer<String, MonitorMessage> createMonitorProducer(
            @Value("${self.producerProps}") String producerPropsFile,
            @Value("${cptls.kafka.bootstrapServers}") String bootstrapServer
    ) {
        log.info("init monitor producer");
        return new KafkaProducer<>(loadProperties(producerPropsFile, bootstrapServer));
    }
}
