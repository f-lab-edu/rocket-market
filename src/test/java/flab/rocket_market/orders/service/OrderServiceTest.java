package flab.rocket_market.orders.service;

import flab.rocket_market.orders.dto.OrderItemRequest;
import flab.rocket_market.orders.dto.OrderItemResponse;
import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentRequest;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.entity.OrderItems;
import flab.rocket_market.orders.entity.Orders;
import flab.rocket_market.orders.entity.Payment;
import flab.rocket_market.orders.enums.OrderStatus;
import flab.rocket_market.orders.exception.OrderFailedException;
import flab.rocket_market.products.entity.Categories;
import flab.rocket_market.products.entity.Products;
import flab.rocket_market.users.entity.Users;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderProcessingService orderProcessingService;

    @InjectMocks
    private OrderService orderService;

    private Categories categories;
    private Products product;
    private OrderItemRequest itemRequest;
    private PaymentRequest paymentRequest;
    private OrderRequest orderRequest;
    private Payment payment;
    private Users user;
    private Orders orders;
    private OrderItems orderItems;
    private PaymentResponse paymentResponse;
    private List<OrderItemResponse> orderItemResponses;
    private OrderResponse orderResponse;

    @BeforeEach
    void setup() {
        categories = createCategories();
        product = createProducts(categories);
        itemRequest = createOrderItemRequest();
        paymentRequest = createPaymentRequest();
        orderRequest = createOrderRequest(itemRequest, paymentRequest);
        payment = createPayment(paymentRequest);
        user = createUsers();
        orders = createOrders(user, payment, orderRequest);
        orderItems = createOrderItems(orders, product, itemRequest);
        paymentResponse = PaymentResponse.of(payment);
        orderItemResponses = createOrderItemResponse(orderItems);
        orderResponse = OrderResponse.of(orders, orderItemResponses, paymentResponse);
    }

    @Test
    @DisplayName("주문 데이터 저장 로직 호출")
    void createOrder() {
        //given
        when(orderProcessingService.processOrder(orderRequest, paymentResponse)).thenReturn(orderResponse);

        //when
        OrderResponse result = orderService.createOrder(orderRequest, paymentResponse);

        //then
        assertThat(result.getTotalPrice()).isEqualTo(paymentRequest.getTotalPrice());
        assertThat(result.getReceiverName()).isEqualTo(orderRequest.getReceiverName());
        assertThat(result.getPaymentInfo().getType()).isEqualTo(paymentRequest.getType());
    }

    @Test
    @DisplayName("주문 데이터 저장 실패 시 복구 작업")
    void recover() {
        //given
        doNothing().when(paymentService).cancelPayment(paymentResponse);
        doNothing().when(orderProcessingService).restoreInventory(orderRequest.getItems());

        //when & then
        Assertions.assertThrows(OrderFailedException.class, () -> orderService.recover(new Exception(), orderRequest, paymentResponse));
        verify(paymentService, times(1)).cancelPayment(paymentResponse);
        verify(orderProcessingService, times(1)).restoreInventory(orderRequest.getItems());
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

    private List<OrderItemResponse> createOrderItemResponse(OrderItems orderItems) {
        ArrayList<OrderItemResponse> list = new ArrayList<>();
        list.add(OrderItemResponse.of(orderItems));
        return list;
    }
}