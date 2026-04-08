package com.br.susreceita.prescription.adapter.out.kafka;

import com.br.susreceita.prescription.adapter.out.kafka.event.PrescriptionRequestEvent;
import com.br.susreceita.prescription.adapter.out.kafka.event.PrescriptionStatusEvent;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.domain.port.out.PrescriptionEventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionKafkaPublisher implements PrescriptionEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PrescriptionKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPrescriptionRequest(Prescription prescription) {
        PrescriptionRequestEvent event = new PrescriptionRequestEvent(
            prescription.getId(),
            prescription.getPatientId(),
            prescription.getCreatedAt()
        );
        // TODO: Configure exact topic name and properties
        kafkaTemplate.send("PRESCRIPTION.REQUEST", event);
    }

    @Override
    public void publishPrescriptionStatus(Prescription prescription) {
        PrescriptionStatusEvent event = new PrescriptionStatusEvent(
            prescription.getId(),
            prescription.getStatus()
        );
        // TODO: Configure exact topic name and properties
        kafkaTemplate.send("PRESCRIPTION.STATUS", event);
    }
}
