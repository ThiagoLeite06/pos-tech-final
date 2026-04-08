package com.br.susreceita.prescription.domain.model;

import java.util.UUID;

public record Prescription(
    UUID id,
    String patientId,
    String medication,
    String status
) {
}
