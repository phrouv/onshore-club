package com.onshore.services.service;

import com.onshore.common_lib.exception.PaymentException;
import com.onshore.common_lib.payload.request.PaymentInitiateRequest;
import com.onshore.common_lib.payload.request.PaymentVerifyRequest;
import com.onshore.common_lib.payload.response.PaymentDTO;
import com.onshore.common_lib.payload.response.PaymentInitiateResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

  PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws PaymentException;

  PaymentDTO verifyPayment(PaymentVerifyRequest request) throws PaymentException;

  Page<PaymentDTO> getAllPayments(Pageable pageable);

  Map<UUID, PaymentDTO> getPaymentsByBookingIds(List<UUID> bookingIds);
}
