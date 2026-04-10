package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.infrastructure.adapter.out.persistence.entity.PrescriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {
    List<PrescriptionEntity> findAllByPatientId(String patientId, Pageable pageable);
}
