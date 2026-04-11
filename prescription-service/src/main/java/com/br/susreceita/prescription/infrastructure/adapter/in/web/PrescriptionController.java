package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.mapper.PrescriptionRequestMapper;
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
    private final PrescriptionRequestMapper prescriptionRequestMapper;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase, PrescriptionRequestMapper prescriptionRequestMapper) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.prescriptionRequestMapper = prescriptionRequestMapper;
    }

    @PostMapping
    public ResponseEntity<Void> createPrescription(@RequestBody PrescriptionRequestDto request) {
        // Map DTO to Domain
        Request prescription = this.prescriptionRequestMapper.toDomain(request);

        // Fire and Forget
        createPrescriptionUseCase.createPrescriptionAsync(prescription);

        return ResponseEntity.accepted().build();
    }
}