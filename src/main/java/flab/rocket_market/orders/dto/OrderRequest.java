package flab.rocket_market.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "{user.id.notnull}")
    private Long userId;

    @NotNull(message = "{order.item.notnull}")
    @Valid
    private List<OrderItemRequest> items;

    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;

    @NotNull(message = "{order.payment.notnull}")
    @Valid
    private PaymentRequest paymentInfo;

}
