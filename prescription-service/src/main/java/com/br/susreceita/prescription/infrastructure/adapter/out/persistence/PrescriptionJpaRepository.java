package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.infrastructure.adapter.out.persistence.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {
    // TODO: Add custom query methods if needed
}
