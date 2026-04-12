package com.br.susreceita.prescription.domain.service;

import com.br.susreceita.prescription.application.port.in.*;
import com.br.susreceita.prescription.application.port.in.mapper.CommandToDomainMapper;
import com.br.susreceita.prescription.application.port.out.DrugRespositoryPort;
import com.br.susreceita.prescription.domain.model.Drug;
import com.br.susreceita.prescription.domain.model.EvidenceStatus;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.application.port.out.PrescriptionEventPublisherPort;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import com.br.susreceita.prescription.domain.model.RequestItem;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.mapper.PrescriptionRequestMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PrescriptionService implements CreatePrescriptionUseCase, ProcessEvidenceStatusUseCase, GetPrescriptionUseCase, ListPatientPrescriptionsUseCase {

    private final PrescriptionRepositoryPort repositoryPort;
    private final PrescriptionEventPublisherPort publisherPort;
    private final PrescriptionRequestMapper prescriptionRequestMapper;
    private final DrugRespositoryPort drugRespositoryPort;
    private final CommandToDomainMapper commandToDomainMapper;

    public PrescriptionService(PrescriptionRepositoryPort repositoryPort, PrescriptionEventPublisherPort publisherPort, PrescriptionRequestMapper prescriptionRequestMapper, DrugRespositoryPort drugRespositoryPort, CommandToDomainMapper commandToDomainMapper) {
        this.repositoryPort = repositoryPort;
        this.publisherPort = publisherPort;
        this.prescriptionRequestMapper = prescriptionRequestMapper;
        this.drugRespositoryPort = drugRespositoryPort;
        this.commandToDomainMapper = commandToDomainMapper;
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
                    savedRequest.getId(),
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
    public void processEvidenceStatus(ProcessEvidenceCommand command) throws SQLDataException {
        Optional<Request> originalRequest =
                this.repositoryPort.findById(command.requestId());

        if (originalRequest.isEmpty()) {
            throw new SQLDataException("Request não encontrado!");
        }

        originalRequest.get().setStatus(command.status());
        originalRequest.get().setDoctorCrm(command.crm());
        originalRequest.get().setItems(
                this.commandToDomainMapper.toRequestItems(
                        command.medicines(),originalRequest.get()));

        if (!isValidated(command.status())) {
            originalRequest.get().setUpdateAt(LocalDateTime.now());
        } else if(isRenewable(command.prescriptionDate()) || originalRequest.get().getAttempts() > 2){
            originalRequest.get().setPrescriptionDate(command.prescriptionDate());
            originalRequest.get().setItems(this.renewableMedicines(originalRequest.get().getItems()));
            originalRequest.get().setStatus(EvidenceStatus.APPROVED);
            originalRequest.get().setUpdateAt(LocalDateTime.now());
        }else{
            originalRequest.get().setStatus(EvidenceStatus.REJECTED);
            originalRequest.get().setUpdateAt(LocalDateTime.now());
        }

        this.repositoryPort.save(originalRequest.get());
        this.publisherPort.publishPrescriptionStatus(originalRequest.get());

    }

    private boolean isRenewable(Date prescriptionDate){
        if (prescriptionDate == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 60);
        Date currentDatePlus60 = calendar.getTime();

        return prescriptionDate.before(currentDatePlus60);
    }

    private List<RequestItem> renewableMedicines(List<RequestItem> medicineList){
        for (RequestItem item : medicineList) {
            Optional<Drug> requestItem = this.drugRespositoryPort.findByName(item.getName());
            if(requestItem.isEmpty() ||
                    requestItem.get().isBlackLabel() ||
                    requestItem.get().isControlled() ||
                    !requestItem.get().isActive()){
                medicineList.remove(item);
            }
        }
        return  medicineList;
    }

    private boolean isValidated(EvidenceStatus status){
        return status == EvidenceStatus.VALIDATED;
    }

    @Override
    public Optional<Request> getPrescription(UUID id) {
        return repositoryPort.findById(id);
    }

    @Override
    public List<Request> listPatientPrescriptions(String patientId, int page, int size) {
        return repositoryPort.findAllByPatientCpf(patientId, page, size);
    }
}