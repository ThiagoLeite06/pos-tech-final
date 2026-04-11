package com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;

import java.util.List;
import java.util.UUID;

public record EvidenceStatusEvent(
    UUID requestId,
    String crm,
    String prescriptionDate,
    List<MedicineEvent> medicine,
    String obs,
    EvidenceStatus status
) {}
