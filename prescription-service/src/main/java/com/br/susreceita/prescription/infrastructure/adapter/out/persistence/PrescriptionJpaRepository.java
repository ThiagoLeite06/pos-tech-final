package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;
import com.br.susreceita.prescription.domain.model.Request;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<Request, UUID> {
    List<Request> findAllByCpf(String cpf, Pageable pageable);
    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING'")
    List<Request> findAllPendingPrescriptions(Pageable pageable);
    @Modifying
    @Transactional
    @Query("update Request r SET r.status = :status where r.id = :id")
    void updateStatus(UUID id, EvidenceStatus status);
}
