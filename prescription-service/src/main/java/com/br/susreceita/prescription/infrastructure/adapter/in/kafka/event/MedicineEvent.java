package com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event;

public record MedicineEvent(
        String name,
        String doses
) {
}
