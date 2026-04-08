package com.br.susreceita.prescription.adapter.in.web;

import com.br.susreceita.prescription.adapter.in.web.dto.PrescriptionRequest;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.domain.port.in.RequestPrescriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final RequestPrescriptionUseCase requestPrescriptionUseCase;

    public PrescriptionController(RequestPrescriptionUseCase requestPrescriptionUseCase) {
        this.requestPrescriptionUseCase = requestPrescriptionUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> requestPrescription(@RequestBody PrescriptionRequest request) {
        // Map DTO to Domain Model
        Prescription prescription = new Prescription(
                UUID.randomUUID(), 
                request.patientId(), 
                "PENDING", 
                LocalDateTime.now()
        );
        
        // Asynchronous call (Fire-and-Forget)
        requestPrescriptionUseCase.requestPrescription(prescription);

        return ResponseEntity.accepted().build(); // 202 Accepted
    }
}
