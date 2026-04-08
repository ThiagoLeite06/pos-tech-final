package com.br.susreceita.prescription.domain.port.out;

import com.br.susreceita.prescription.domain.model.Prescription;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepositoryPort {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(UUID id);
}
