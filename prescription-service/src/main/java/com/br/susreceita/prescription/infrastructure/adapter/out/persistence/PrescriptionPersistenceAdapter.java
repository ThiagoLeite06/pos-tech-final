package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PrescriptionPersistenceAdapter implements PrescriptionRepositoryPort {

    private final PrescriptionJpaRepository repository;

    public PrescriptionPersistenceAdapter(PrescriptionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Request save(Request request) {
        return repository.save(request);
    }

    @Override
    public Optional<Request> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Request> findAllByPatientCpf(String cpf, int page, int size) {
        return repository.findAllByCpf(cpf, PageRequest.of(page, size));
    }
}