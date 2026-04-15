package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;

import java.util.UUID;

public interface ReviewPrescriptionUseCase {
    void reviewPrescription(UUID id, EvidenceStatus status);
}
