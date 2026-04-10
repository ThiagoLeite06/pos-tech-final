package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Prescription;

import java.util.List;

public interface ListPatientPrescriptionsUseCase {
    List<Prescription> listPatientPrescriptions(String patientId, int page, int size);
}
