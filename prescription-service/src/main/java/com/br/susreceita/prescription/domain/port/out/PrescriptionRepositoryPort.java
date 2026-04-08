package com.br.susreceita.prescription.domain.port.out;

import com.br.susreceita.prescription.domain.model.Prescription;
import java.util.Optional;

public interface PrescriptionRepositoryPort {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(String id);
}
