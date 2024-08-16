package flab.rocket_market.service;

import flab.rocket_market.controller.dto.ProductResponse;
import flab.rocket_market.controller.dto.RegisterProductRequest;
import flab.rocket_market.controller.dto.UpdateProductRequest;
import flab.rocket_market.entity.Categories;
import flab.rocket_market.entity.Products;
import flab.rocket_market.exception.CategoryNotFoundException;
import flab.rocket_market.exception.ProductNotFoundException;
import flab.rocket_market.repository.CategoryRepository;
import flab.rocket_market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public void registerProduct(RegisterProductRequest productRequest) {
        Categories category = getCategory(productRequest.getCategoryId());

        Products product = Products.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .category(category)
                .build();

        productRepository.save(product);
    }

    public ProductResponse getProductById(Long productId) {
        Products product = getProduct(productId);

        return ProductResponse.of(product);
    }

    @Transactional
    public void updateProduct(UpdateProductRequest productRequest) {
        Products product = getProduct(productRequest.getProductId());

        Categories category = null;
        if (productRequest.getCategoryId() != null) {
            category = getCategory(productRequest.getCategoryId());
        }

        product.updateProduct(
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getPrice(),
                category);
    }

    public void deleteProduct(Long productId) {
        Products product = getProduct(productId);

        productRepository.delete(product);
    }

    private Products getProduct(Long productId) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> ProductNotFoundException.EXCEPTION);
        return product;
    }

    private Categories getCategory(Long categoryId) {
        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryNotFoundException.EXCEPTION);
        return category;
    }
}
