package com.br.susreceita.prescription.domain.port.in;

public interface ProcessEvidenceStatusUseCase {
    void processEvidenceStatus(String evidenceId, String status);
}
