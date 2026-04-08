package com.br.susreceita.prescription.infrastructure.adapter.in.kafka;

import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;
import com.br.susreceita.prescription.application.port.in.ProcessEvidenceStatusUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EvidenceStatusListener {

    private final ProcessEvidenceStatusUseCase processEvidenceStatusUseCase;

    public EvidenceStatusListener(ProcessEvidenceStatusUseCase processEvidenceStatusUseCase) {
        this.processEvidenceStatusUseCase = processEvidenceStatusUseCase;
    }

    @KafkaListener(topics = "EVIDENCE.STATUS", groupId = "prescription-service-group")
    public void consumeEvidenceStatus(EvidenceStatusEvent event) {
        // TODO: Map event to Domain input
        processEvidenceStatusUseCase.processEvidenceStatus(event.evidenceId(), event.status());
    }
}
