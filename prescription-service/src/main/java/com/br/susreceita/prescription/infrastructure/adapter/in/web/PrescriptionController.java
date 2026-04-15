package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.application.port.in.*;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.ReviewPrescriptionRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receitas")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final GetPrescriptionUseCase getPrescriptionUseCase;
    private final ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase;
    private final ListPrescriptionsInPendingReviewUseCase listPrescriptionsInPendingReviewUseCase;
    private final ReviewPrescriptionUseCase reviewPrescriptionUseCase;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase, GetPrescriptionUseCase getPrescriptionUseCase, ListPatientPrescriptionsUseCase listPatientPrescriptionsUseCase, ListPrescriptionsInPendingReviewUseCase listPrescriptionsInPendingReviewUseCase, ReviewPrescriptionUseCase reviewPrescriptionUseCase) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
        this.getPrescriptionUseCase = getPrescriptionUseCase;
        this.listPatientPrescriptionsUseCase = listPatientPrescriptionsUseCase;
        this.listPrescriptionsInPendingReviewUseCase = listPrescriptionsInPendingReviewUseCase;
        this.reviewPrescriptionUseCase = reviewPrescriptionUseCase;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping(value = "/solicitacoes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PreAuthorize("hasAnyRole('PATIENT', 'REVIEWER')")
    @GetMapping("/{id}")
    public ResponseEntity<Request> findById(@PathVariable UUID id){
        return ResponseEntity.of(getPrescriptionUseCase.getPrescription(id));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping
    public ResponseEntity<List<Request>> findAllByPatientId(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size){

        String cpf = jwt.getClaim("cpf");

        List<Request> prescriptions = listPatientPrescriptionsUseCase.listPatientPrescriptions(cpf, page, size);
        return ResponseEntity.ok(prescriptions);
    }

    @PreAuthorize("hasRole('REVIEWER')")
    @PatchMapping("/solicitacoes/{id}/revisar")
    public ResponseEntity<Void> reviewPrescription(@PathVariable UUID id, @RequestBody ReviewPrescriptionRequestDto dto){

        reviewPrescriptionUseCase.reviewPrescription(id, dto.status().getStatus());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    @GetMapping("/revisao")
    public ResponseEntity<List<Request>> findAllPendingPrescriptions(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        List<Request> prescriptions = listPrescriptionsInPendingReviewUseCase.listPrescriptionsInPendingReview(page, size);

        return ResponseEntity.ok(prescriptions);
    }
}