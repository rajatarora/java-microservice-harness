package com.etree.harness.example.service;

import java.util.List;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;

public interface ProductService {

    ProductResponseDto create(ProductCreateDto dto);

    ProductResponseDto getById(Long id);

    List<ProductResponseDto> getAll();

    ProductResponseDto update(Long id, ProductUpdateDto dto);

    void delete(Long id);

}
