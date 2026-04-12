package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.Drug;

import java.util.Optional;
import java.util.UUID;

public interface DrugRespositoryPort {
    Optional<Drug> findByName(String name);
}
