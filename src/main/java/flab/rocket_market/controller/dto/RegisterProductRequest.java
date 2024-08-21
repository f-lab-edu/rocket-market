package flab.rocket_market.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
}
