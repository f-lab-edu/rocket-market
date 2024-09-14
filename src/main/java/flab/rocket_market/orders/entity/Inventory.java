package flab.rocket_market.orders.entity;

import flab.rocket_market.products.entity.Products;
import flab.rocket_market.orders.exception.OutOfStockException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Products products;

    private Integer quantity;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void add(int quantity) {
        this.quantity += quantity;
    }

    public void decrease(int quantity) {
        if (this.quantity - quantity < 0) {
            throw OutOfStockException.EXCEPTION;
        }
        this.quantity -= quantity;
    }
}
