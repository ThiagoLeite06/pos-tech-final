package com.br.susreceita.prescription.infrastructure.adapter.in.kafka;

import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;
import com.br.susreceita.prescription.application.port.in.ProcessEvidenceStatusUseCase;
import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.mapper.EvidenceStatusEventMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.SQLDataException;

@Component
public class EvidenceStatusListener {

    private final ProcessEvidenceStatusUseCase processEvidenceStatusUseCase;
    private final EvidenceStatusEventMapper eventMapper;

    public EvidenceStatusListener(ProcessEvidenceStatusUseCase processEvidenceStatusUseCase, EvidenceStatusEventMapper eventMapper) {
        this.processEvidenceStatusUseCase = processEvidenceStatusUseCase;
        this.eventMapper = eventMapper;
    }

    @KafkaListener(topics = "EVIDENCE.STATUS", groupId = "prescription-service-group")
    public void consumeEvidenceStatus(EvidenceStatusEvent event) throws SQLDataException {
        var command = eventMapper.toCommand(event);
        processEvidenceStatusUseCase.processEvidenceStatus(command);
    }
}