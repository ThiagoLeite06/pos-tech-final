package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<Request, UUID> {
    List<Request> findAllByCpf(String cpf, Pageable pageable);
}
