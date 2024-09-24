package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.PaymentRequest;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.entity.Payment;
import flab.rocket_market.orders.exception.PaymentNotFoundException;
import flab.rocket_market.orders.exception.PaymentProcessingException;
import flab.rocket_market.orders.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment processPayment(PaymentRequest paymentRequest) {

        boolean isSuccessful = callPaymentAPI();

        if (!isSuccessful) {
            throw PaymentProcessingException.EXCEPTION;
        }

        Payment payment = paymentRepository.save(Payment.builder()
                .type(paymentRequest.getType())
                .totalPrice(paymentRequest.getTotalPrice())
                .status(isSuccessful)
                .build());

        return payment;
    }

    public void cancelPayment(PaymentResponse paymentResponse) {
        boolean isSuccessful = callPaymentAPI();

        if (!isSuccessful) {
            throw PaymentProcessingException.EXCEPTION;
        }

        Payment payment = paymentRepository.findById(paymentResponse.getPaymentId())
                .orElseThrow(() -> PaymentNotFoundException.EXCEPTION);

        payment.failedPayment();
    }

    /**
     * 추후 외부 결제 API 호출
     */
    public boolean callPaymentAPI() {
        return true;
    }
}
