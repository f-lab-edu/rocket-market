package flab.rocket_market.controller;

import flab.rocket_market.controller.dto.PageResponse;
import flab.rocket_market.controller.dto.ProductResponse;
import flab.rocket_market.controller.dto.RegisterProductRequest;
import flab.rocket_market.controller.dto.UpdateProductRequest;
import flab.rocket_market.global.response.BaseDataResponse;
import flab.rocket_market.global.response.BaseResponse;
import flab.rocket_market.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static flab.rocket_market.global.message.MessageConstants.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public BaseDataResponse<ProductResponse> registerProduct(@Valid @RequestBody RegisterProductRequest productRequest) {
        ProductResponse product = productService.registerProduct(productRequest);
        return BaseDataResponse.of(HttpStatus.CREATED, PRODUCT_REGISTER_SUCCESS.getMessage(), product);
    }

    @GetMapping("/{productId}")
    public BaseDataResponse<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return BaseDataResponse.of(HttpStatus.OK, PRODUCT_RETRIEVE_SUCCESS.getMessage(), product);
    }

    @PatchMapping
    public BaseResponse updateProduct(@Valid @RequestBody UpdateProductRequest productRequest) {
        productService.updateProduct(productRequest);
        return BaseResponse.of(HttpStatus.OK, PRODUCT_UPDATE_SUCCESS.getMessage());
    }

    @DeleteMapping("/{productId}")
    public BaseResponse deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return BaseResponse.of(HttpStatus.NO_CONTENT, PRODUCT_DELETE_SUCCESS.getMessage());
    }

    @GetMapping
    public BaseDataResponse<PageResponse<ProductResponse>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageResponse<ProductResponse> pageResponse = productService.getProducts(page, size);
        return BaseDataResponse.of(HttpStatus.OK, PRODUCT_RETRIEVE_SUCCESS.getMessage(), pageResponse);
    }

    @GetMapping("/search")
    public BaseDataResponse<PageResponse<ProductResponse>> searchProducts(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageResponse<ProductResponse> pageResponse = productService.searchProducts(keyword, page, size);
        return BaseDataResponse.of(HttpStatus.OK, PRODUCT_RETRIEVE_SUCCESS.getMessage(), pageResponse);
    }
}

