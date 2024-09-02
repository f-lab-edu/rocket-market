package flab.rocket_market.service;

import flab.rocket_market.aop.RequiredRole;
import flab.rocket_market.dto.PageResponse;
import flab.rocket_market.dto.ProductResponse;
import flab.rocket_market.dto.RegisterProductRequest;
import flab.rocket_market.dto.UpdateProductRequest;
import flab.rocket_market.entity.Categories;
import flab.rocket_market.entity.Products;
import flab.rocket_market.exception.CategoryNotFoundException;
import flab.rocket_market.exception.ProductNotFoundException;
import flab.rocket_market.repository.CategoryRepository;
import flab.rocket_market.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @RequiredRole("ROLE_ADMIN")
    public ProductResponse registerProduct(RegisterProductRequest productRequest) {
        Categories category = getCategory(productRequest.getCategoryId());

        Products product = Products.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .category(category)
                .build();

        Products savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    public ProductResponse getProductById(Long productId) {
        Products product = getProduct(productId);

        return ProductResponse.of(product);
    }

    @Transactional
    @RequiredRole("ROLE_ADMIN")
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

    @RequiredRole("ROLE_ADMIN")
    public void deleteProduct(Long productId) {
        Products product = getProduct(productId);

        productRepository.delete(product);
    }

    public PageResponse<ProductResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Products> productsPage = productRepository.findAll(pageable);

        return getPageResponse(productsPage);
    }

    public PageResponse<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Products> productsPage = productRepository.findByNameContaining(keyword, pageable);

        return getPageResponse(productsPage);
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

    private PageResponse<ProductResponse> getPageResponse(Page<Products> productsPage) {
        List<ProductResponse> productResponses = productsPage.getContent().stream()
                .map(products -> ProductResponse.of(products))
                .collect(Collectors.toList());

        return new PageResponse<>(
                productResponses,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.isLast()
        );
    }
}
