package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.BlockedDrug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlockedDrugJpaRespository extends JpaRepository<BlockedDrug, UUID> {

}
