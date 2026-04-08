package com.br.susreceita.prescription.application.port.in;

public interface ProcessEvidenceStatusUseCase {
    void processEvidenceStatus(String prescriptionId, String status);
}
