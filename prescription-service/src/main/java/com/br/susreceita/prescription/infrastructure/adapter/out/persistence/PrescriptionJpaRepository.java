package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<Request, UUID> {
    // TODO: Add custom query methods if needed
}
