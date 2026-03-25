package com.etree.harness.example.mapper;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

public interface ProductMapper {

    Product toEntity(ProductCreateDto dto);

    void updateFromDto(ProductUpdateDto dto, Product entity);

    ProductResponseDto toDto(Product entity);

}
