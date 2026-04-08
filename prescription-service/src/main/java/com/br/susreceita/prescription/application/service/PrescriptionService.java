package com.br.susreceita.prescription.application.service;

import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.domain.port.in.ProcessEvidenceStatusUseCase;
import com.br.susreceita.prescription.domain.port.in.RequestPrescriptionUseCase;
import com.br.susreceita.prescription.domain.port.out.PrescriptionEventPublisherPort;
import com.br.susreceita.prescription.domain.port.out.PrescriptionRepositoryPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService implements RequestPrescriptionUseCase, ProcessEvidenceStatusUseCase {

    private final PrescriptionRepositoryPort repositoryPort;
    private final PrescriptionEventPublisherPort publisherPort;

    public PrescriptionService(PrescriptionRepositoryPort repositoryPort, PrescriptionEventPublisherPort publisherPort) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
    }

    @Async
    @Override
    public void requestPrescription(Prescription prescription) {
        // TODO: Implement business logic to validate and process prescription request
        // e.g., save to DB, then publish event
        Prescription saved = repositoryPort.save(prescription);
        publisherPort.publishPrescriptionRequest(saved);
    }

    @Override
    public void processEvidenceStatus(String evidenceId, String status) {
        // TODO: Implement business logic for processing evidence status updates
        // e.g., update prescription status in DB, publish status event
    }
}
