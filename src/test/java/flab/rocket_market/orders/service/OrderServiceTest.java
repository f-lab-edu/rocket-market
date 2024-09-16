package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.OrderItemRequest;
import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentRequest;
import flab.rocket_market.orders.entity.Inventory;
import flab.rocket_market.orders.entity.OrderItems;
import flab.rocket_market.orders.entity.Orders;
import flab.rocket_market.orders.entity.Payment;
import flab.rocket_market.orders.enums.OrderStatus;
import flab.rocket_market.orders.exception.OutOfStockException;
import flab.rocket_market.orders.exception.PaymentProcessingException;
import flab.rocket_market.orders.repository.InventoryRepository;
import flab.rocket_market.orders.repository.OrderItemRepository;
import flab.rocket_market.orders.repository.OrderRepository;
import flab.rocket_market.products.entity.Categories;
import flab.rocket_market.products.entity.Products;
import flab.rocket_market.products.exception.ProductNotFoundException;
import flab.rocket_market.products.repository.ProductRepository;
import flab.rocket_market.users.entity.Users;
import flab.rocket_market.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    private Categories categories;
    private Products product;
    private Inventory inventory;
    private OrderItemRequest itemRequest;
    private PaymentRequest paymentRequest;
    private OrderRequest orderRequest;
    private Payment payment;
    private Users user;
    private Orders orders;
    private OrderItems orderItems;

    @BeforeEach
    void setup() {
        categories = createCategories();
        product = createProducts(categories);
        inventory = createInventory(product);
        itemRequest = createOrderItemRequest();
        paymentRequest = createPaymentRequest();
        orderRequest = createOrderRequest(itemRequest, paymentRequest);
        payment = createPayment(paymentRequest);
        user = createUsers();
        orders = createOrders(user, payment, orderRequest);
        orderItems = createOrderItems(orders, product, itemRequest);
    }

    @Test
    @DisplayName("상품 주문")
    void createOrder() {
        //given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProducts(product)).thenReturn(inventory);
        when(paymentService.processPayment(paymentRequest)).thenReturn(payment);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Orders.class))).thenReturn(orders);
        when(orderItemRepository.save(any(OrderItems.class))).thenReturn(orderItems);

        //when
        OrderResponse result = orderService.createOrder(orderRequest);

        //then
        assertThat(inventory.getQuantity()).isEqualTo(9); // 재고 감소 확인
        verify(orderRepository, times(1)).save(any(Orders.class));
        verify(orderItemRepository, times(1)).save(any(OrderItems.class));
        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));

        assertThat(result.getTotalPrice()).isEqualTo(paymentRequest.getTotalPrice());
        assertThat(result.getReceiverName()).isEqualTo(orderRequest.getReceiverName());
        assertThat(result.getPaymentInfo().getType()).isEqualTo(paymentRequest.getType());
    }

    @Test
    @DisplayName("재고 부족으로 인한 주문 실패")
    void createOrderOutOfStock() {
        //given
        inventory.decrease(inventory.getQuantity()); // 재고를 0으로 설정

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProducts(product)).thenReturn(inventory);

        //when & then
        assertThrows(OutOfStockException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 인한 주문 실패")
    void createOrderProductNotFound() {
        //given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    @DisplayName("결제 실패로 인한 주문 실패")
    void createOrderPaymentError() {
        //given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProducts(product)).thenReturn(inventory);
        when(paymentService.processPayment(paymentRequest)).thenThrow(PaymentProcessingException.EXCEPTION);

        //when & then
        assertThrows(PaymentProcessingException.class, () -> orderService.createOrder(orderRequest));
    }

    private OrderItems createOrderItems(Orders orders, Products product, OrderItemRequest itemRequest) {
        return OrderItems.builder()
                .orders(orders)
                .products(product)
                .quantity(itemRequest.getQuantity())
                .price(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                .build();
    }

    private Orders createOrders(Users user, Payment payment, OrderRequest orderRequest) {
        return Orders.builder()
                .users(user)
                .payment(payment)
                .totalPrice(payment.getTotalPrice())
                .receiverName(orderRequest.getReceiverName())
                .receiverAddress(orderRequest.getReceiverAddress())
                .receiverPhone(orderRequest.getReceiverPhone())
                .status(OrderStatus.PENDING)
                .build();
    }

    private Users createUsers() {
        return Users.builder()
                .userId(1L)
                .email("test@gmail.com")
                .username("테스트 유저")
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

    private OrderRequest createOrderRequest(OrderItemRequest itemRequest, PaymentRequest paymentRequest) {
        return OrderRequest.builder()
                .userId(1L)
                .receiverName("수신자")
                .receiverAddress("수신자 주소")
                .receiverPhone("수신자 연락처")
                .items(Arrays.asList(itemRequest))
                .paymentInfo(paymentRequest)
                .build();
    }

    private PaymentRequest createPaymentRequest() {
        return PaymentRequest.builder()
                .type("카드 결제")
                .totalPrice(BigDecimal.valueOf(10000))
                .build();
    }

    private OrderItemRequest createOrderItemRequest() {
        return OrderItemRequest.builder()
                .productId(1L)
                .quantity(1)
                .build();
    }

    private Inventory createInventory(Products product) {
        return Inventory.builder()
                .products(product)
                .inventoryId(1L)
                .quantity(10)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Products createProducts(Categories categories) {
        return Products.builder()
                .productId(1L)
                .name("티셔츠")
                .description("티셔츠 입니다.")
                .price(BigDecimal.valueOf(5000))
                .category(categories)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Categories createCategories() {
        return Categories.builder()
                .categoryId(1L)
                .name("의류")
                .description("패션 의류")
                .build();
    }
}