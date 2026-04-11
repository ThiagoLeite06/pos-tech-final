package com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;

import java.util.UUID;

public record PrescriptionStatusEvent(
    UUID prescriptionId,
    EvidenceStatus status
) {}
