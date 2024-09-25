package flab.rocket_market.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.rocket_market.orders.dto.OrderItemRequest;
import flab.rocket_market.orders.dto.OrderItemResponse;
import flab.rocket_market.orders.dto.OrderRequest;
import flab.rocket_market.orders.dto.OrderResponse;
import flab.rocket_market.orders.dto.PaymentRequest;
import flab.rocket_market.orders.dto.PaymentResponse;
import flab.rocket_market.orders.enums.OrderStatus;
import flab.rocket_market.orders.exception.OrderFailedException;
import flab.rocket_market.orders.exception.OutOfStockException;
import flab.rocket_market.orders.exception.PaymentProcessingException;
import flab.rocket_market.orders.service.OrderProcessingService;
import flab.rocket_market.orders.service.OrderService;
import flab.rocket_market.products.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderProcessingService orderProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private OrderItemRequest itemRequest;
    private PaymentRequest paymentRequest;
    private OrderRequest orderRequest;
    private OrderItemResponse orderItemResponse;
    private PaymentResponse paymentResponse;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        itemRequest = createOrderItemRequest();
        paymentRequest = createPaymentRequest();
        orderRequest = createOrderRequest(itemRequest, paymentRequest);
        orderItemResponse = createOrderItemResponse();
        paymentResponse = createPaymentResponse();
        orderResponse = createOrderResponse(orderItemResponse, paymentResponse);
    }

    @Test
    @DisplayName("상품 주문")
    void createOrder() throws Exception {
        //given
        given(orderProcessingService.processPayment(any())).willReturn(paymentResponse);
        given(orderService.createOrder(any(), any())).willReturn(orderResponse);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(document("create-order",
                        resourceDetails().description("상품 주문"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getOrderRequestFields()),
                        responseFields(getOrderResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("재고 부족으로 인한 주문 실패")
    void createOrderOutOfStock() throws Exception {
        //given
        given(orderProcessingService.processPayment(any())).willThrow(OutOfStockException.EXCEPTION);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(document("create-order-out-of-stock",
                        resourceDetails().description("재고 부족으로 인한 주문 실패"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getOrderRequestFields()),
                        responseFields(getBaseResponseFields())
                )).andDo(print())
                .andExpect(jsonPath("$.message").value(OutOfStockException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 인한 주문 실패")
    void createOrderProductNotFound() throws Exception {
        //given
        given(orderProcessingService.processPayment(any())).willThrow(ProductNotFoundException.EXCEPTION);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(document("create-order-product-not-found",
                        resourceDetails().description("존재하지 않는 상품으로 인한 주문 실패"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getOrderRequestFields()),
                        responseFields(getBaseResponseFields())
                )).andDo(print())
                .andExpect(jsonPath("$.message").value(ProductNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("결제 실패로 인한 주문 실패")
    void createOrderPaymentError() throws Exception {
        //given
        given(orderProcessingService.processPayment(any())).willThrow(PaymentProcessingException.EXCEPTION);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(document("create-order-payment-error",
                        resourceDetails().description("결제 실패로 인한 주문 실패"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getOrderRequestFields()),
                        responseFields(getBaseResponseFields())
                )).andDo(print())
                .andExpect(jsonPath("$.message").value(PaymentProcessingException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("주문 데이터 저장 실패로 인한 주문 실패")
    void createOrderOrderFailedException() throws Exception {
        //given
        given(orderProcessingService.processPayment(any())).willReturn(paymentResponse);
        given(orderService.createOrder(any(), any())).willThrow(OrderFailedException.EXCEPTION);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(document("create-order-payment-error",
                        resourceDetails().description("주문 데이터 저장 실패로 인한 주문 실패"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getOrderRequestFields()),
                        responseFields(getBaseResponseFields())
                )).andDo(print())
                .andExpect(jsonPath("$.message").value(OrderFailedException.EXCEPTION.getError().getMessage()));
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

    private OrderResponse createOrderResponse(OrderItemResponse orderItemResponse, PaymentResponse paymentResponse) {
        return OrderResponse.builder()
                .orderId(1L)
                .status(OrderStatus.PENDING.getValue())
                .totalPrice(BigDecimal.valueOf(1000))
                .receiverName("수신자")
                .receiverAddress("수신자 주소")
                .receiverPhone("수신자 연락처")
                .items(Arrays.asList(orderItemResponse))
                .paymentInfo(paymentResponse)
                .build();
    }

    private PaymentResponse createPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1L)
                .type("카드 결제")
                .totalPrice(BigDecimal.valueOf(1000))
                .status(true)
                .build();
    }

    private OrderItemResponse createOrderItemResponse() {
        return OrderItemResponse.builder()
                .productId(1L)
                .name("티셔츠")
                .quantity(10)
                .price(BigDecimal.valueOf(1000))
                .build();
    }

    private List<FieldDescriptor> getOrderRequestFields() {
        return List.of(
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                fieldWithPath("receiverName").type(JsonFieldType.STRING).description("수신자 이름"),
                fieldWithPath("receiverAddress").type(JsonFieldType.STRING).description("수신자 주소"),
                fieldWithPath("receiverPhone").type(JsonFieldType.STRING).description("수신자 연락처"),
                fieldWithPath("items[].productId").type(JsonFieldType.NUMBER).description("품목 ID"),
                fieldWithPath("items[].quantity").type(JsonFieldType.NUMBER).description("품목 주문 수량"),
                fieldWithPath("paymentInfo.type").type(JsonFieldType.STRING).description("결제 수단"),
                fieldWithPath("paymentInfo.totalPrice").type(JsonFieldType.NUMBER).description("최종 결제 금액")
        );
    }

    private List<FieldDescriptor> getOrderResponseFields() {
        return List.of(
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 번호"),
                fieldWithPath("data.status").type(JsonFieldType.STRING).description("주문 상태"),
                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("최종 결제 금액"),
                fieldWithPath("data.receiverName").type(JsonFieldType.STRING).description("수신자"),
                fieldWithPath("data.receiverAddress").type(JsonFieldType.STRING).description("수신자 주소"),
                fieldWithPath("data.receiverPhone").type(JsonFieldType.STRING).description("수신자 연락처"),
                fieldWithPath("data.items[].productId").type(JsonFieldType.NUMBER).description("품목 ID"),
                fieldWithPath("data.items[].name").type(JsonFieldType.STRING).description("품목명"),
                fieldWithPath("data.items[].quantity").type(JsonFieldType.NUMBER).description("품목 주문 수량"),
                fieldWithPath("data.items[].price").type(JsonFieldType.NUMBER).description("가격"),
                fieldWithPath("data.paymentInfo.paymentId").type(JsonFieldType.NUMBER).description("결제 정보 ID"),
                fieldWithPath("data.paymentInfo.type").type(JsonFieldType.STRING).description("결제 수단"),
                fieldWithPath("data.paymentInfo.totalPrice").type(JsonFieldType.NUMBER).description("최종 결제 금액"),
                fieldWithPath("data.paymentInfo.status").type(JsonFieldType.BOOLEAN).description("결제 상태")
        );
    }

    private List<FieldDescriptor> getBaseResponseFields() {
        return List.of(
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
        );
    }
}