package com.br.susreceita.evidence.domain.model;

import java.util.List;

public record RecognitionPrescription(
        String crm,
        String prescriptionDate,
        List<MedicineEvent> medicine,
        String obs
) {
}
