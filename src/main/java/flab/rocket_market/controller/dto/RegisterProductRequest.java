package flab.rocket_market.controller.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RegisterProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
}
