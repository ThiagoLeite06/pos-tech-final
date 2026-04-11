package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.EvidenceStatus;
import com.br.susreceita.prescription.domain.model.RequestItem;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ProcessEvidenceCommand(
    UUID requestId,
    String crm,
    Date prescriptionDate,
    List<MedicineCommand> medicines,
    String obs,
    EvidenceStatus status
) { }
