package com.br.susreceita.prescription.domain.service;

import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import com.br.susreceita.prescription.application.port.in.ProcessEvidenceStatusUseCase;
import com.br.susreceita.prescription.application.port.out.PrescriptionEventPublisherPort;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;
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
    public void createPrescriptionAsync(Request request) {
        // TODO: Implement domain logic for creating prescription
        // Example: Save to DB
        // repositoryPort.save(prescription);
        
        // Example: Publish event
        // publisherPort.publishPrescriptionRequest(prescription);
    }

    @Override
    public void processEvidenceStatus(EvidenceStatusEvent event) {
        // TODO: Implement domain logic for processing evidence status
        // Example: Find in DB, update status, save, and publish new status
    }
}
