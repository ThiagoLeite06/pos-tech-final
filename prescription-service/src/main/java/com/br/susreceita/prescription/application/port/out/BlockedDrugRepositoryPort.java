package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.domain.model.BlockedDrug;

import java.util.List;
import java.util.UUID;

public interface BlockedDrugRepositoryPort {
    List<BlockedDrug> findAllBlockedDrugs(int page, int size);
    BlockedDrug save (BlockedDrug blockedDrug);
    boolean delete(UUID id);
}
