package com.br.susreceita.prescription.application.port.in;

import java.util.UUID;

public interface DeleteBlockedDrugUseCase {
    boolean deleteBlockedDrug(UUID id);
}
