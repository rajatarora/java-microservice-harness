package com.etree.harness.example.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

/**
 * DTO used for partial updates to a product. All fields are optional and
 * only provided values should be applied.
 */
public class ProductUpdateDto {

    private String name;

    private String description;

    private BigDecimal price;

    @PositiveOrZero
    private Integer stock;

}
