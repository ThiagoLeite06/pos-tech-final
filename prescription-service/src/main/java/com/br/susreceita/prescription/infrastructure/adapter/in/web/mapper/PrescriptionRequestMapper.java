package com.br.susreceita.prescription.infrastructure.adapter.in.web.mapper;

import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionRequestMapper {

    public Request toDomain(PrescriptionRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Request request = new Request();
        request.setFullName(dto.fullName());
        request.setCpf(dto.cpf());
        request.setNumSusCard(dto.numSusCard());

        return request;
    }
}
