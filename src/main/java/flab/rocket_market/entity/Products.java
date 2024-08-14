package flab.rocket_market.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import flab.rocket_market.global.util.ValueUtils;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @CreatedDate
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public void updateProduct(String name, String description, BigDecimal price, Categories category) {
        this.name = ValueUtils.getNonNullValue(name, this.name);
        this.description = ValueUtils.getNonNullValue(description, this.description);
        this.price = ValueUtils.getNonNullValue(price, this.price);
        this.category = ValueUtils.getNonNullValue(category, this.category);
    }
}
