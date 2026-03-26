package com.etree.harness.example.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;

class ProductCreateDtoValidationTest {

    @Test
    void validProductCreateDto_ShouldPassValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test Product");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(10);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullName_ShouldFailValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName(null);
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(10);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void blankName_ShouldFailValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("   ");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(10);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void nullPrice_ShouldFailValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test");
        dto.setDescription("Description");
        dto.setPrice(null);
        dto.setStock(10);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be null");
    }

    @Test
    void nullStock_ShouldFailValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(null);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be null");
    }

    @Test
    void negativeStock_ShouldFailValidation() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test");
        dto.setDescription("Description");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(-1);

        Set<ConstraintViolation<ProductCreateDto>> violations = ValidationTestUtil.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("greater than or equal to 0");
    }

}
