package com.br.susreceita.prescription.infrastructure.adapter.in.web.dto;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;

public enum ReviewStatus {
    APPROVED(EvidenceStatus.APPROVED),
    REJECTED(EvidenceStatus.REJECTED);

    private final EvidenceStatus status;
    ReviewStatus(EvidenceStatus status) {
        this.status = status;
    }

    public EvidenceStatus getStatus() {
        return status;
    }
}
