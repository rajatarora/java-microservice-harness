package com.etree.harness.example.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDto {

    private String name;

    private String description;

    private BigDecimal price;

    @PositiveOrZero
    private Integer stock;

}
