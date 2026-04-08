package com.br.susreceita.prescription.adapter.out.persistence;

import com.br.susreceita.prescription.adapter.out.persistence.entity.PrescriptionEntity;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.domain.port.out.PrescriptionRepositoryPort;
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
    public Prescription save(Prescription prescription) {
        PrescriptionEntity entity = new PrescriptionEntity(
            prescription.id(),
            prescription.patientId(),
            prescription.medication(),
            prescription.status()
        );
        PrescriptionEntity saved = repository.save(entity);
        return new Prescription(saved.getId(), saved.getPatientId(), saved.getMedication(), saved.getStatus());
    }

    @Override
    public Optional<Prescription> findById(String id) {
        return repository.findById(UUID.fromString(id))
            .map(entity -> new Prescription(
                entity.getId(),
                entity.getPatientId(),
                entity.getMedication(),
                entity.getStatus()
            ));
    }
}