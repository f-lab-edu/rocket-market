package flab.rocket_market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.rocket_market.dto.PageResponse;
import flab.rocket_market.dto.ProductResponse;
import flab.rocket_market.dto.RegisterProductRequest;
import flab.rocket_market.dto.UpdateProductRequest;
import flab.rocket_market.exception.CategoryNotFoundException;
import flab.rocket_market.exception.ProductNotFoundException;
import flab.rocket_market.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                        responseFields(getProductResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("품목 저장")
    void registerProduct() throws Exception {
        //given
        RegisterProductRequest request = getRegisterProductRequest();

        given(productService.registerProduct(any())).willReturn(createProduct(1L));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(document("save-product",
                            resourceDetails().description("품목 생성"),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(getProductRequestFields()),
                            responseFields(getProductResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("품목 수정")
    void updateProduct() throws Exception {
        //given
        UpdateProductRequest request = getUpdateProductRequest();

        willDoNothing().given(productService).updateProduct(any());

        List<FieldDescriptor> productRequestFields = getProductRequestFields();
        productRequestFields.add(fieldWithPath("productId").type(JsonFieldType.NUMBER).description("품목 ID"));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("update-product",
                        resourceDetails().description("품목 수정"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(productRequestFields),
                        responseFields(getBaseResponseFields())
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
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("단건 품목 조회 - 품목이 없는 경우 에러")
    void getProductByIdNotFound() throws Exception {
        //given
        given(productService.getProductById(any())).willThrow(ProductNotFoundException.EXCEPTION);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/{productId}", 1L))
                .andDo(document("find-product-not-found",
                        resourceDetails().description("품목 조회 실패"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("productId").description("품목 ID")),
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ProductNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("품목 저장 - 카테고리가 없는 경우 에러")
    void registerProductCategoryNotFound() throws Exception {
        //given
        RegisterProductRequest request = getRegisterProductRequest();

        given(productService.registerProduct(any())).willThrow(CategoryNotFoundException.EXCEPTION);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("save-product-category-not-found",
                        resourceDetails().description("품목 생성 실패 - 카테고리 없음"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(getProductRequestFields()),
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(CategoryNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("품목 수정 - 품목이 없는 경우 에러")
    void updateProductNotFound() throws Exception {
        //given
        UpdateProductRequest request = getUpdateProductRequest();

        willThrow(ProductNotFoundException.EXCEPTION).given(productService).updateProduct(any());

        List<FieldDescriptor> productRequestFields = getProductRequestFields();
        productRequestFields.add(fieldWithPath("productId").type(JsonFieldType.NUMBER).description("품목 ID"));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("update-product-not-found",
                        resourceDetails().description("품목 수정 실패 - 품목 없음"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(productRequestFields),
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ProductNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("품목 수정 - 카테고리가 없는 경우 에러")
    void updateProductCategoryNotFound() throws Exception {
        //given
        UpdateProductRequest request = getUpdateProductRequest();

        willThrow(CategoryNotFoundException.EXCEPTION).given(productService).updateProduct(any());

        List<FieldDescriptor> productRequestFields = getProductRequestFields();
        productRequestFields.add(fieldWithPath("productId").type(JsonFieldType.NUMBER).description("품목 ID"));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("update-product-not-found",
                        resourceDetails().description("품목 수정 실패 - 카테고리 없음"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(productRequestFields),
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(CategoryNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("품목 삭제 - 품목이 없는 경우 에러")
    void deleteProductNotFound() throws Exception {
        //given
        willThrow(ProductNotFoundException.EXCEPTION).given(productService).deleteProduct(any());

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/products/{productId}", 1L))
                .andDo(document("delete-product",
                        resourceDetails().description("품목 삭제 실패 - 품목 없음"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("productId").description("품목 ID")),
                        responseFields(getBaseResponseFields())
                ))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ProductNotFoundException.EXCEPTION.getError().getMessage()));
    }

    @Test
    @DisplayName("품목 전체 조회")
    void getProducts() throws Exception {
        //given
        given(productService.getProducts(anyInt(), anyInt())).willReturn(getPageResponse(1L, 2L));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(document("find-products",
                        resourceDetails().description("품목 전체 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(getPageResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("품목 검색")
    void searchProducts() throws Exception {
        //given
        given(productService.searchProducts(anyString(), anyInt(), anyInt())).willReturn(getPageResponse(1L, 2L));

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/search")
                        .param("keyword", "키워드")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(document("find-products",
                        resourceDetails().description("품목 전체 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").description("검색 키워드"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(getPageResponseFields())
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    private RegisterProductRequest getRegisterProductRequest() {
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name("품목명")
                .description("품목 설명")
                .price(BigDecimal.valueOf(0))
                .categoryId(0L)
                .build();
        return request;
    }

    private UpdateProductRequest getUpdateProductRequest() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .productId(1L)
                .name("품목명")
                .description("품목 설명")
                .price(BigDecimal.valueOf(0))
                .categoryId(0L)
                .build();
        return request;
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

    private PageResponse<ProductResponse> getPageResponse(Long ...productId) {
        List<ProductResponse> list = new ArrayList<>();
        for (Long id : productId) {
            ProductResponse product = createProduct(id);
            list.add(product);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponse> productsPage = new PageImpl<>(list, pageable, list.size());

        return new PageResponse<>(
                list,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.isLast()
        );
    }

    private List<FieldDescriptor> getProductRequestFields() {
        return List.of(
            fieldWithPath("name").type(JsonFieldType.STRING).description("품목명"),
            fieldWithPath("description").type(JsonFieldType.STRING).description("품목 설명"),
            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
            fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
        );
    }

    private List<FieldDescriptor> getBaseResponseFields() {
        return List.of(
            fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
        );
    }

    private List<FieldDescriptor> getProductResponseFields() {
        List<FieldDescriptor> list = getBaseResponseFields();
        list.add(fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("품목 ID"));
        list.add(fieldWithPath("data.name").type(JsonFieldType.STRING).description("품목명"));
        list.add(fieldWithPath("data.description").type(JsonFieldType.STRING).description("품목 설명"));
        list.add(fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"));
        list.add(fieldWithPath("data.categoryName").type(JsonFieldType.STRING).description("카테고리명"));
        list.add(fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성일 (ISO 8601 형식)"));
        list.add(fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정일 (ISO 8601 형식)"));
        return list;
    }

    private List<FieldDescriptor> getPageResponseFields() {
        List<FieldDescriptor> list = getBaseResponseFields();
        list.add(fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER).description("품목 ID"));
        list.add(fieldWithPath("data.content[].name").type(JsonFieldType.STRING).description("품목명"));
        list.add(fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("품목 설명"));
        list.add(fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"));
        list.add(fieldWithPath("data.content[].categoryName").type(JsonFieldType.STRING).description("카테고리명"));
        list.add(fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("생성일 (ISO 8601 형식)"));
        list.add(fieldWithPath("data.content[].updatedAt").type(JsonFieldType.STRING).description("수정일 (ISO 8601 형식)"));
        list.add(fieldWithPath("data.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"));
        list.add(fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"));
        list.add(fieldWithPath("data.totalElement").type(JsonFieldType.NUMBER).description("전체 품목 개수"));
        list.add(fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"));
        list.add(fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"));
        return list;
    }
}