package flab.rocket_market.orders.dto;

import flab.rocket_market.orders.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private String type;
    private BigDecimal totalPrice;
    private boolean status;

    public static PaymentResponse of(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .type(payment.getType())
                .totalPrice(payment.getTotalPrice())
                .status(payment.getStatus())
                .build();
    }
}
