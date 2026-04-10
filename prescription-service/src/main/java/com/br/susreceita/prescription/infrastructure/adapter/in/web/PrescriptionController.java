package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.application.port.in.GetPrescriptionUseCase;
import com.br.susreceita.prescription.application.port.in.ListPatientPrescriptionsUseCase;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequest;
import com.br.susreceita.prescription.domain.model.Prescription;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;
    private final ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase, GetPrescriptionUseCase getPrescriptionUseCase, ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
        this.listPatientPrescriptionsUseCase = listPatientPrescriptionsUseCase;
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

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> findById(@PathVariable String id){
        return ResponseEntity.of(getPrescriptionUseCase.getPrescription(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Prescription>> findAllByPatientId(@PathVariable String id, @RequestParam("page") int page,
                                                                 @RequestParam("size") int size){

        List<Prescription> prescriptions = listPatientPrescriptionsUseCase.listPatientPrescriptions(id, page, size);
        return ResponseEntity.ok(prescriptions);
    }

}