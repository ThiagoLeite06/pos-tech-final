package com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event;

import java.util.UUID;

public record PrescriptionStatusEvent(
    UUID prescriptionId,
    String status
    // TODO: Add other relevant fields
) {}
