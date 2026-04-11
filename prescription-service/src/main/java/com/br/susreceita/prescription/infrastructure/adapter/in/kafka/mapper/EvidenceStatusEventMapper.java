package com.br.susreceita.prescription.infrastructure.adapter.in.kafka.mapper;

import com.br.susreceita.prescription.application.port.in.ProcessEvidenceCommand;
import com.br.susreceita.prescription.application.port.in.MedicineCommand;
import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.EvidenceStatusEvent;
import com.br.susreceita.prescription.infrastructure.adapter.in.kafka.event.MedicineEvent;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EvidenceStatusEventMapper {

    public ProcessEvidenceCommand toCommand(EvidenceStatusEvent event) {
        if (event == null) {
            return null;
        }

        List<MedicineCommand> medicines = Collections.emptyList();
        if (event.medicine() != null) {
            medicines = event.medicine().stream()
                .map(this::toMedicineCommand)
                .collect(Collectors.toList());
        }

        return new ProcessEvidenceCommand(
            event.requestId(),
            event.crm(),
            Timestamp.valueOf(event.prescriptionDate()),
            medicines,
            event.obs(),
            event.status()
        );
    }

    private MedicineCommand toMedicineCommand(MedicineEvent medicineEvent) {
        if (medicineEvent == null) {
            return null;
        }
        return new MedicineCommand(
            medicineEvent.name(),
            medicineEvent.doses()
        );
    }
}