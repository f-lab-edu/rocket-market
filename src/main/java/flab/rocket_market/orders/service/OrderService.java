package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.exception.OrderFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderProcessingService orderProcessingService;
    private final PaymentService paymentService;

    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000),
        retryFor = {Exception.class}
    )
    public OrderResponse createOrder(OrderRequest orderRequest, PaymentResponse paymentResponse) {
        return orderProcessingService.processOrder(orderRequest, paymentResponse);
    }

    @Recover
    public OrderResponse recover(Exception e, OrderRequest orderRequest, PaymentResponse paymentResponse) {
        paymentService.cancelPayment(paymentResponse); // 결제 취소
        orderProcessingService.restoreInventory(orderRequest.getItems()); //재고 복구

        throw OrderFailedException.EXCEPTION;
    }
}
