package com.br.susreceita.evidence.domain.model;

import java.util.List;
import java.util.UUID;

public record Evidence(
        UUID requestId,
        String crm,
        String prescriptionDate,
        List<MedicineEvent> medicine,
        String obs,
        EvidenceStatus status
) {}
