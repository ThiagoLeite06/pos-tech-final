package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.application.port.in.*;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;
    private final ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase;
    private final ListPrescriptionsInPendingReviewUseCase listPrescriptionsInPendingReviewUseCase;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase, GetPrescriptionUseCase getPrescriptionUseCase, ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase, ListPrescriptionsInPendingReviewUseCase listPrescriptionsInPendingReviewUseCase) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
        this.listPatientPrescriptionsUseCase = listPatientPrescriptionsUseCase;
        this.listPrescriptionsInPendingReviewUseCase = listPrescriptionsInPendingReviewUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPrescription(@RequestPart("file") MultipartFile file,
                                                     @RequestPart("prescription") PrescriptionRequestDto request) {
        try{
            byte[] bytes = file.getBytes();
            String imageAsBase64 = Base64.getEncoder().encodeToString(bytes);

            // Map DTO to Command
            var command = new CreatePrescriptionCommand(
                    null,
                    request.fullName(),
                    request.cpf(),
                    request.numSusCard(),
                    imageAsBase64,
                    request.mimeType()
            );

            // Fire and Forget
            createPrescriptionUseCase.createPrescriptionAsync(command);

        } catch (IOException e){
            return ResponseEntity.internalServerError().body("Error uploading file");
        }
        return ResponseEntity.accepted().body("Renovação de prescrição enviada para analise.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> findById(@PathVariable UUID id){
        return ResponseEntity.of(getPrescriptionUseCase.getPrescription(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Request>> findAllByPatientId(@PathVariable String id,
                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size){

        List<Request> prescriptions = listPatientPrescriptionsUseCase.listPatientPrescriptions(id, page, size);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/review")
    public ResponseEntity<List<Request>> findAllPendingPrescriptions(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        List<Request> prescriptions = listPrescriptionsInPendingReviewUseCase.listPrescriptionsInPendingReview(page, size);

        return ResponseEntity.ok(prescriptions);
    }
}