package flab.rocket_market.controller;

import flab.rocket_market.controller.dto.ProductResponse;
import flab.rocket_market.controller.dto.RegisterProductRequest;
import flab.rocket_market.controller.dto.UpdateProductRequest;
import flab.rocket_market.global.response.BaseDataResponse;
import flab.rocket_market.global.response.BaseResponse;
import flab.rocket_market.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public BaseDataResponse<ProductResponse> registerProduct(@RequestBody RegisterProductRequest productRequest) {
        ProductResponse product = productService.registerProduct(productRequest);
        return BaseDataResponse.of(HttpStatus.CREATED, "성공적으로 등록되었습니다.", product);
    }

    @GetMapping("/{productId}")
    public BaseDataResponse<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return BaseDataResponse.of(HttpStatus.OK, "성공적으로 조회되었습니다.", product);
    }

    @PatchMapping
    public BaseResponse updateProduct(@RequestBody UpdateProductRequest productRequest) {
        productService.updateProduct(productRequest);
        return BaseResponse.of(HttpStatus.OK, "성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{productId}")
    public BaseResponse deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return BaseResponse.of(HttpStatus.NO_CONTENT, "성공적으로 삭제되었습니다.");
    }
}
