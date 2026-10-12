package com.pm.analyticsservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(
            topics = "patient",
            groupId = "analytics-service"
    )
    public void consumeEvent(ConsumerRecord<String, Object> record) {

        Object value = record.value();

        log.info("Kafka topic: {}", record.topic());
        log.info("Kafka value runtime type: {}",
                value == null ? "null" : value.getClass().getName());

        if (!(value instanceof byte[] event)) {
            log.error("Expected byte[] but received: {}",
                    value == null ? "null" : value.getClass().getName());
            return;
        }

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            log.info(
                    "Received Patient Event: PatientId={}, Name={}, Email={}",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail()
            );

        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            log.error("Failed to parse PatientEvent", e);
        }
    }
}