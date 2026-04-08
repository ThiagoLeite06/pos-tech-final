package com.br.susreceita.prescription.adapter.out.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionRequestEvent(
    UUID prescriptionId,
    String patientId,
    String medication,
    LocalDateTime requestedAt
    // TODO: Add other relevant fields
) {}
