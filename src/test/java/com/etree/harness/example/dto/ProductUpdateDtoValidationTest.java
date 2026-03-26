package com.etree.harness.example.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;

class ProductUpdateDtoValidationTest {

    @Test
    void validProductUpdateDto_ShouldPassValidation() {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("Test Product");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(10);

        Set<ConstraintViolation<ProductUpdateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void allNullFields_ShouldPassValidation() {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName(null);
        dto.setDescription(null);
        dto.setPrice(null);
        dto.setStock(null);

        Set<ConstraintViolation<ProductUpdateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void negativeStock_ShouldFailValidation() {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("Test");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(-5);

        Set<ConstraintViolation<ProductUpdateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("greater than or equal to 0");
    }

}
