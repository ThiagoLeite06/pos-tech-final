package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequest;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createPrescription(@RequestBody PrescriptionRequest request) {
        // Map DTO to Domain
        Prescription prescription = new Prescription(
            UUID.randomUUID(),
            request.patientId(),
            request.medicationDetails(),
            "PENDING"
        );

        // Fire and Forget
        createPrescriptionUseCase.createPrescriptionAsync(prescription);

        return ResponseEntity.accepted().build();
    }
}