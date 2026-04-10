package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Prescription;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepositoryPort {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(String id);
    List<Prescription> findAllByPatientId(String id, int page, int size);
}
