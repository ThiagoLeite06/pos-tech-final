package com.br.susreceita.prescription.domain.service;

import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import com.br.susreceita.prescription.application.port.in.ProcessEvidenceStatusUseCase;
import com.br.susreceita.prescription.application.port.out.PrescriptionEventPublisherPort;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService implements CreatePrescriptionUseCase, ProcessEvidenceStatusUseCase {

    private final PrescriptionRepositoryPort repositoryPort;
    private final PrescriptionEventPublisherPort publisherPort;

    public PrescriptionService(PrescriptionRepositoryPort repositoryPort, PrescriptionEventPublisherPort publisherPort) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
    }

    @Async
    @Override
    public void createPrescriptionAsync(Prescription prescription) {
        // TODO: Implement domain logic for creating prescription
        // Example: Save to DB
        // repositoryPort.save(prescription);
        
        // Example: Publish event
        // publisherPort.publishPrescriptionRequest(prescription);
    }

    @Override
    public void processEvidenceStatus(String prescriptionId, String status) {
        // TODO: Implement domain logic for processing evidence status
        // Example: Find in DB, update status, save, and publish new status
    }
}
