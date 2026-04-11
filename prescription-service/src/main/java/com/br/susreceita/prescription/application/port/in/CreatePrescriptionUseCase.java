package com.br.susreceita.prescription.application.port.in;

public interface CreatePrescriptionUseCase {
    void createPrescriptionAsync(CreatePrescriptionCommand command);
}
