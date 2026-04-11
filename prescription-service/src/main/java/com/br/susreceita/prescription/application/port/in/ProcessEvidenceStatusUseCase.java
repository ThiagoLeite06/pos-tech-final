package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;

public interface ProcessEvidenceStatusUseCase {
    void processEvidenceStatus(EvidenceStatusEvent event);
}
