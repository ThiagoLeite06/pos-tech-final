package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.application.port.out.DrugRespositoryPort;
import com.br.susreceita.prescription.domain.model.Drug;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DrugPersistenceAdapter implements DrugRespositoryPort {

    private final DrugJpaRespository repository;

    public DrugPersistenceAdapter(DrugJpaRespository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Drug> findByName(String name) {

        return Optional.empty();
    }
}
