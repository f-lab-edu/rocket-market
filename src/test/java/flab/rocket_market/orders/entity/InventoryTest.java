package flab.rocket_market.orders.entity;

import flab.rocket_market.orders.exception.OutOfStockException;
import flab.rocket_market.products.entity.Categories;
import flab.rocket_market.products.entity.Products;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class InventoryTest {

    private static final int QUANTITY = 10;

    private Categories categories;
    private Products products;
    private Inventory inventory;

    @BeforeEach
    void setup() {
        categories = createCategories();
        products = createProducts(categories);
        inventory = createInventory(products);
    }

    @Test
    @DisplayName("재고 수량 증가 - 동시성 테스트")
    void increase() throws InterruptedException {
        //given
        int threads = 3;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        //when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                inventory.increase(1);
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        Assertions.assertThat(inventory.getQuantity()).isEqualTo(QUANTITY + threads);
    }

    @Test
    @DisplayName("재고 수량 감소 - 동시성 테스트")
    void decrease() throws InterruptedException {
        //given
        int threads = 3;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        //when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    inventory.decrease(1);

                } catch (OutOfStockException e) {
                    System.out.println("재고 부족");

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        Assertions.assertThat(inventory.getQuantity()).isEqualTo(QUANTITY - threads);

    }

    private Inventory createInventory(Products product) {
        return Inventory.builder()
                .products(product)
                .inventoryId(1L)
                .quantity(QUANTITY)
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