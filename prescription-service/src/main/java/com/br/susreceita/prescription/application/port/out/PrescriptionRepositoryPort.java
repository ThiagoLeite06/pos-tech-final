package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Request;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepositoryPort {
    Request save(Request request);
    Optional<Request> findById(UUID id);
}
