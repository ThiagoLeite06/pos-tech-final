package com.br.susreceita.prescription.infrastructure.adapter.out.kafka;

import com.br.susreceita.prescription.application.port.in.CreatePrescriptionCommand;
import com.br.susreceita.prescription.domain.model.EvidenceStatus;
import com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event.PrescriptionRequestEvent;
import com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event.PrescriptionStatusEvent;
import com.br.susreceita.prescription.domain.model.Request;
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
    public void publishPrescriptionRequest(CreatePrescriptionCommand command) {
        PrescriptionRequestEvent event = new PrescriptionRequestEvent(
            command.requestId(),
            command.file()
        );
        kafkaTemplate.send("PRESCRIPTION.REQUEST", event);
    }

    @Override
    public void publishPrescriptionStatus(Request request) {
        PrescriptionStatusEvent event = new PrescriptionStatusEvent(
            request.getRequestId(),
            EvidenceStatus.PENDING
        );
        kafkaTemplate.send("PRESCRIPTION.STATUS", event);
    }
}