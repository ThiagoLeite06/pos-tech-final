package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.infrastructure.adapter.out.persistence.entity.PrescriptionEntity;
import com.br.susreceita.prescription.application.port.out.PrescriptionRepositoryPort;
import org.springframework.stereotype.Component;

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
        PrescriptionEntity entity = new PrescriptionEntity();
        PrescriptionEntity saved = repository.save(entity);
        return new Request();
    }

    @Override
    public Optional<Request> findById(String id) {
        return null;
    }
}