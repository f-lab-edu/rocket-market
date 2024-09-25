package flab.rocket_market.orders.controller;

import flab.rocket_market.global.response.BaseDataResponse;
import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.service.OrderProcessingService;
import flab.rocket_market.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static flab.rocket_market.global.message.MessageConstants.ORDER_CREATE_SUCCESS;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderProcessingService orderProcessingService;

    @PostMapping
    public BaseDataResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        PaymentResponse paymentResponse = orderProcessingService.processPayment(orderRequest);
        OrderResponse order = orderService.createOrder(orderRequest, paymentResponse);

        return BaseDataResponse.of(HttpStatus.CREATED, ORDER_CREATE_SUCCESS.getMessage(), order);
    }
}
