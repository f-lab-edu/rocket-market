package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.OrderItemRequest;
import flab.rocket_market.orders.dto.OrderItemResponse;
import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.entity.Inventory;
import flab.rocket_market.orders.entity.OrderItems;
import flab.rocket_market.orders.entity.Orders;
import flab.rocket_market.orders.entity.Payment;
import flab.rocket_market.orders.enums.OrderStatus;
import flab.rocket_market.orders.exception.OutOfStockException;
import flab.rocket_market.orders.repository.InventoryRepository;
import flab.rocket_market.orders.repository.OrderItemRepository;
import flab.rocket_market.orders.repository.OrderRepository;
import flab.rocket_market.products.entity.Products;
import flab.rocket_market.products.exception.ProductNotFoundException;
import flab.rocket_market.products.repository.ProductRepository;
import flab.rocket_market.users.exception.UserNotFoundException;
import flab.rocket_market.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final PaymentService paymentService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        //재고 파악
        for (OrderItemRequest orderItem : orderRequest.getItems()) {
            Products products = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> ProductNotFoundException.EXCEPTION);

            Inventory inventory = inventoryRepository.findByProducts(products);

            if (inventory == null || inventory.getQuantity() < orderItem.getQuantity()) {
                throw OutOfStockException.EXCEPTION;
            }

            //재고 감소
            inventory.decrease(orderItem.getQuantity());
        }

        //결제 API 호출
        Payment payment = paymentService.processPayment(orderRequest.getPaymentInfo());

        //주문 데이터 생성 및 저장
        Orders orders = orderRepository.save(Orders.builder()
                .users(userRepository.findById(orderRequest.getUserId())
                        .orElseThrow(() -> UserNotFoundException.EXCEPTION))
                .payment(payment)
                .totalPrice(payment.getTotalPrice())
                .receiverName(orderRequest.getReceiverName())
                .receiverAddress(orderRequest.getReceiverAddress())
                .receiverPhone(orderRequest.getReceiverPhone())
                .status(OrderStatus.PENDING)
                .build());

        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderItemRequest orderItem : orderRequest.getItems()) {
            Products products = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> ProductNotFoundException.EXCEPTION);

            OrderItems orderItems = orderItemRepository.save(OrderItems.builder()
                    .orders(orders)
                    .products(products)
                    .quantity(orderItem.getQuantity())
                    .price(products.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                    .build());

            items.add(OrderItemResponse.of(orderItems));
        }

        return OrderResponse.of(orders, items, PaymentResponse.of(payment));
    }
}
