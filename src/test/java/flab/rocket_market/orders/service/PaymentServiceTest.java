package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.PaymentRequest;
import flab.rocket_market.orders.entity.Payment;
import flab.rocket_market.orders.exception.PaymentProcessingException;
import flab.rocket_market.orders.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = createPaymentRequest();
        payment = createPayment(paymentRequest);
    }

    @Test
    @DisplayName("결제 서비스 호출")
    void processPayment() {
        //given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        //when
        Payment result = paymentService.processPayment(paymentRequest);

        //then
        assertThat(result.getPaymentId()).isEqualTo(payment.getPaymentId());
    }

    @Test
    @DisplayName("결제 실패")
    void processPaymentError() {
        //given
        PaymentService paymentServiceSpy = Mockito.spy(paymentService);
        doReturn(false).when(paymentServiceSpy).callPaymentAPI();

        //when & then
        assertThrows(PaymentProcessingException.class, () -> paymentServiceSpy.processPayment(paymentRequest));
    }

    private PaymentRequest createPaymentRequest() {
        return PaymentRequest.builder()
                .type("카드 결제")
                .totalPrice(BigDecimal.valueOf(10000))
                .build();
    }

    private Payment createPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .paymentId(1L)
                .type(paymentRequest.getType())
                .totalPrice(paymentRequest.getTotalPrice())
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}