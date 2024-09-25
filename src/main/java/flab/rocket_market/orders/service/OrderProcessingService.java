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
import flab.rocket_market.orders.exception.PaymentNotFoundException;
import flab.rocket_market.orders.repository.InventoryRepository;
import flab.rocket_market.orders.repository.OrderItemRepository;
import flab.rocket_market.orders.repository.OrderRepository;
import flab.rocket_market.orders.repository.PaymentRepository;
import flab.rocket_market.products.entity.Products;
import flab.rocket_market.products.exception.ProductNotFoundException;
import flab.rocket_market.products.repository.ProductRepository;
import flab.rocket_market.users.exception.UserNotFoundException;
import flab.rocket_market.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final PaymentService paymentService;

    @Transactional
    public PaymentResponse processPayment(OrderRequest orderRequest) {
        decreaseInventory(orderRequest.getItems()); // 재고 확인 및 감소
        Payment payment = paymentService.processPayment(orderRequest.getPaymentInfo()); // 결제

        return PaymentResponse.of(payment);
    }

    @Transactional
    public OrderResponse processOrder(OrderRequest orderRequest, PaymentResponse paymentResponse) {
        Orders orders = saveOrders(orderRequest, paymentResponse.getPaymentId());
        List<OrderItemResponse> items = saveOrderItems(orderRequest.getItems(), orders);

        return OrderResponse.of(orders, items, paymentResponse);
    }

    public Orders saveOrders(OrderRequest orderRequest, long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> PaymentNotFoundException.EXCEPTION);

        return orderRepository.save(Orders.builder()
                .users(userRepository.findById(orderRequest.getUserId())
                        .orElseThrow(() -> UserNotFoundException.EXCEPTION))
                .payment(payment)
                .totalPrice(payment.getTotalPrice())
                .receiverName(orderRequest.getReceiverName())
                .receiverAddress(orderRequest.getReceiverAddress())
                .receiverPhone(orderRequest.getReceiverPhone())
                .status(OrderStatus.PENDING)
                .build());
    }

    public List<OrderItemResponse> saveOrderItems(List<OrderItemRequest> orderItemRequests, Orders orders) {
        List<OrderItemResponse> items = new ArrayList<>();

        int cnt = 0;
        for (OrderItemRequest orderItem : orderItemRequests) {
            Products products = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> ProductNotFoundException.EXCEPTION);

            if(cnt++ == 1) {
                throw new RuntimeException();
            }

            OrderItems orderItems = orderItemRepository.save(OrderItems.builder()
                    .orders(orders)
                    .products(products)
                    .quantity(orderItem.getQuantity())
                    .price(products.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                    .build());

            items.add(OrderItemResponse.of(orderItems));
        }

        return items;
    }

    public void decreaseInventory(List<OrderItemRequest> orderItemRequests) {
        for (OrderItemRequest orderItem : orderItemRequests) {
            Products products = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> ProductNotFoundException.EXCEPTION);

            Inventory inventory = inventoryRepository.findByProducts(products);

            if (inventory == null || inventory.getQuantity() < orderItem.getQuantity()) {
                throw OutOfStockException.EXCEPTION;
            }

            inventory.decrease(orderItem.getQuantity());
        }
    }

    @Transactional
    public void restoreInventory(List<OrderItemRequest> orderItemRequests) {
        for (OrderItemRequest orderItem : orderItemRequests) {
            Products products = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> ProductNotFoundException.EXCEPTION);

            Inventory inventory = inventoryRepository.findByProducts(products);

            inventory.increase(orderItem.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}
