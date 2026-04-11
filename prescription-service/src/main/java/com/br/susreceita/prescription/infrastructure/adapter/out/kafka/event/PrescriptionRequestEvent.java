package com.br.susreceita.prescription.infrastructure.adapter.out.kafka.event;

import java.util.UUID;

public record PrescriptionRequestEvent(
    UUID prescriptionId,
    String image64
) {}
