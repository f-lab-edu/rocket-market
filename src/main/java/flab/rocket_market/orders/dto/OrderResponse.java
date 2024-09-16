package flab.rocket_market.orders.dto;

import flab.rocket_market.orders.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String status;
    private BigDecimal totalPrice;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private List<OrderItemResponse> items;
    private PaymentResponse paymentInfo;

    public static OrderResponse of(Orders orders, List<OrderItemResponse> items, PaymentResponse payment) {
        return OrderResponse.builder()
                .orderId(orders.getOrderId())
                .status(orders.getStatus().getValue())
                .totalPrice(orders.getTotalPrice())
                .receiverName(orders.getReceiverName())
                .receiverAddress(orders.getReceiverAddress())
                .receiverPhone(orders.getReceiverPhone())
                .items(items)
                .paymentInfo(payment)
                .build();
    }
}
