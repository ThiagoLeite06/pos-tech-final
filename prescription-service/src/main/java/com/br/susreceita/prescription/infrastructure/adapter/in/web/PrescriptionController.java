package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.PrescriptionRequestDto;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionCommand;
import com.br.susreceita.prescription.application.port.in.CreatePrescriptionUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;

    public PrescriptionController(CreatePrescriptionUseCase createPrescriptionUseCase) {
        this.createPrescriptionUseCase = createPrescriptionUseCase;
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
}