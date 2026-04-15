package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Request;

import java.util.Optional;
import java.util.UUID;

public interface GetPrescriptionUseCase {
    Optional<Request> getPrescription(UUID uuid);
}
