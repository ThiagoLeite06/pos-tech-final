package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Request;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepositoryPort {
    Request save(Request request);
    Optional<Request> findById(UUID id);
    List<Request> findAllByPatientCpf(String cpf, int page, int size);
    List<Request> findAllPendingPrescriptions(int page, int size);
}
