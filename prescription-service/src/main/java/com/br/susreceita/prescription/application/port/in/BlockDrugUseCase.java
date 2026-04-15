package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.BlockedDrug;

public interface BlockDrugUseCase {
    void blockDrug(BlockedDrug blockedDrug);
}
