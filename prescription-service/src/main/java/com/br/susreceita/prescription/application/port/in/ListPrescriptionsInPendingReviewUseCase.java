package com.br.susreceita.prescription.application.port.in;

import com.br.susreceita.prescription.domain.model.Request;

import java.util.List;

public interface ListPrescriptionsInPendingReviewUseCase {

    List<Request> listPrescriptionsInPendingReview(int page, int size);
}
