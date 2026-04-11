package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.domain.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DrugJpaRespository extends JpaRepository<Drug, UUID> {

    @Query("select d from Drug d where d.brandName = ?1")
    Optional<Drug> findByName(String name);
}
