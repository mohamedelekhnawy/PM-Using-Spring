package org.elekhnawy.patient_service.kafka;

import org.elekhnawy.patient_service.Model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String,byte[]> kafkaTemplate) {
        this.kafkaTemplate= kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        kafkaTemplate.send("patient", event.toByteArray())
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message successfully with offset=[{}]", result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send message due to : {}", ex.getMessage(), ex);
                    }
                });
    }
}
