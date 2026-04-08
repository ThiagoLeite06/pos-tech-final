package com.br.susreceita.prescription.infrastructure.adapter.out.kafka;

import com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event.PrescriptionRequestEvent;
import com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event.PrescriptionStatusEvent;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.application.port.out.PrescriptionEventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class PrescriptionKafkaPublisher implements PrescriptionEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PrescriptionKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPrescriptionRequest(Prescription prescription) {
        PrescriptionRequestEvent event = new PrescriptionRequestEvent(
            prescription.id(),
            prescription.patientId(),
            prescription.medication(),
            LocalDateTime.now()
        );
        kafkaTemplate.send("PRESCRIPTION.REQUEST", event);
    }

    @Override
    public void publishPrescriptionStatus(Prescription prescription) {
        PrescriptionStatusEvent event = new PrescriptionStatusEvent(
            prescription.id(),
            prescription.status()
        );
        kafkaTemplate.send("PRESCRIPTION.STATUS", event);
    }
}