package com.br.susreceita.prescription.infrastructure.adapter.in.web.mapper;

import com.br.susreceita.prescription.application.port.in.CreatePrescriptionCommand;
import com.br.susreceita.prescription.domain.model.EvidenceStatus;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionRequestMapper {

    public Request toDomain(CreatePrescriptionCommand command) {
        if (command == null) {
            return null;
        }

        Request request = new Request();
        request.setFullName(command.fullName());
        request.setCpf(command.cpf());
        request.setNumSusCard(command.numSusCard());
        request.setStatus(EvidenceStatus.PENDING);

        return request;
    }
}
