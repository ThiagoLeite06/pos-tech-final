package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Request;

import java.util.List;

public interface ListPatientPrescriptionsUseCase {
    List<Request> listPatientPrescriptions(String cpf, int page, int size);
}
