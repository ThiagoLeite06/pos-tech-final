package com.br.susreceita.prescription.domain.service;

import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionCommand;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import com.br.susreceita.prescription.application.port.in.ProcessEvidenceStatusUseCase;
import com.br.susreceita.prescription.application.port.out.PrescriptionEventPublisherPort;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.mapper.PrescriptionRequestMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService implements CreatePrescriptionUseCase, ProcessEvidenceStatusUseCase {

    private final PrescriptionRepositoryPort repositoryPort;
    private final PrescriptionEventPublisherPort publisherPort;
    private final PrescriptionRequestMapper prescriptionRequestMapper;

    public PrescriptionService(PrescriptionRepositoryPort repositoryPort, PrescriptionEventPublisherPort publisherPort, PrescriptionRequestMapper prescriptionRequestMapper) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
        this.prescriptionRequestMapper = prescriptionRequestMapper;
    }

    @Async
    @Override
    public void createPrescriptionAsync(CreatePrescriptionCommand command) {
        // 1. Create the Domain Entity for the DB
        Request request = this.prescriptionRequestMapper.toDomain(command);

        try {
            // 2. Save and retrieve the saved entity with generated ID
            Request savedRequest = this.repositoryPort.save(request);

            // 3. Create a new command record with the updated requestId
            CreatePrescriptionCommand updatedCommand = new CreatePrescriptionCommand(
                savedRequest.getRequestId(),
                command.fullName(),
                command.cpf(),
                command.numSusCard(),
                command.file(),
                command.mimeType()
            );

            // 4. Publish to Kafka with the ID included
            this.publisherPort.publishPrescriptionRequest(updatedCommand);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processEvidenceStatus(EvidenceStatusEvent event) {
        // TODO: Implement domain logic for processing evidence status
        // Example: Find in DB, update status, save, and publish new status
    }
}
