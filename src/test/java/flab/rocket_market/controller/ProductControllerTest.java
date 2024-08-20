package flab.rocket_market.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.rocket_market.controller.dto.ProductResponse;
import flab.rocket_market.controller.dto.RegisterProductRequest;
import flab.rocket_market.controller.dto.UpdateProductRequest;
import flab.rocket_market.service.ProductService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductControllerTest {

    @MockBean
    protected ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("품목 저장")
    void registerProduct() throws Exception {
        //given
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name("품목명")
                .description("품목 설명")
                .price(BigDecimal.valueOf(0))
                .categoryId(0L)
                .build();

        given(productService.registerProduct(any())).willReturn(createProduct(1L));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(document("save-product",
                            resourceDetails().description("품목 생성"),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("품목명"),
                                    fieldWithPath("description").type(JsonFieldType.STRING).description("품목 설명"),
                                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                    fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                            ),
                            responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                    fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("품목 ID"),
                                    fieldWithPath("data.name").type(JsonFieldType.STRING).description("품목명"),
                                    fieldWithPath("data.description").type(JsonFieldType.STRING).description("품목 설명"),
                                    fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                    fieldWithPath("data.categoryName").type(JsonFieldType.STRING).description("카테고리명"),
                                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일"),
                                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정일")
                            )
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("단건 품목 조회")
    void getProductById() throws Exception {
        //given
        given(productService.getProductById(any())).willReturn(createProduct(1L));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/{productId}", 1L))
                .andDo(document("find-product",
                                resourceDetails().description("품목 조회"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(parameterWithName("productId").description("품목 ID")),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                        fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("품목 ID"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("품목명"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("품목 설명"),
                                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.categoryName").type(JsonFieldType.STRING).description("카테고리명"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일 (ISO 8601 형식)"),
                                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정일 (ISO 8601 형식)")
                                )
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("품목 수정")
    void updateProduct() throws Exception {
        //given
        UpdateProductRequest request = UpdateProductRequest.builder()
                .productId(1L)
                .name("품목명")
                .description("품목 설명")
                .price(BigDecimal.valueOf(0))
                .categoryId(0L)
                .build();

        willDoNothing().given(productService).updateProduct(any());

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("update-product",
                        resourceDetails().description("품목 수정"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("품목 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("품목명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("품목 설명"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                        )
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("품목 삭제")
    void deleteProduct() throws Exception {
        //given
        willDoNothing().given(productService).deleteProduct(any());

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/products/{productId}", 1L))
                .andDo(document("delete-product",
                        resourceDetails().description("품목 삭제"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("productId").description("품목 ID")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                        )
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    private ProductResponse createProduct(Long productId) {
        return ProductResponse.builder()
                .productId(productId)
                .name("품목명")
                .description("품목 설명")
                .price(BigDecimal.valueOf(0))
                .categoryName("카테고리명")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}