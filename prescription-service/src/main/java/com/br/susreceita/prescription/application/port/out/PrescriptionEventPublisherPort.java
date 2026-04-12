package com.br.susreceita.prescription.application.port.out;

import com.br.susreceita.prescription.application.port.in.CreatePrescriptionCommand;
import com.br.susreceita.prescription.domain.model.Request;

public interface PrescriptionEventPublisherPort {
    void publishPrescriptionRequest(CreatePrescriptionCommand command);
    void publishPrescriptionStatus(Request request);
}
