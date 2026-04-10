package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Prescription;

import java.util.Optional;

public interface GetPrescriptionUseCase {
    Optional<Prescription> getPrescription(String id);
}
