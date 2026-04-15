package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.BlockedDrug;

import java.util.List;

public interface ListBlockedDrugsUseCase {
    List<BlockedDrug> listBlockedDrugs(int page, int size);
}
