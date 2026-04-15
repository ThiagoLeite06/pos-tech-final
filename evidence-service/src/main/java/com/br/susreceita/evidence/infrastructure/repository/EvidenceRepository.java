package com.br.susreceita.evidence.infrastructure.repository;

import com.br.susreceita.evidence.infrastructure.entity.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Long> {
}