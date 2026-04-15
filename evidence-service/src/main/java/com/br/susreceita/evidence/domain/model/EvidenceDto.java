package com.br.susreceita.evidence.domain.model;

import java.util.UUID;

public record EvidenceDto(
        UUID prescriptionId,
        String image64
) {
}
