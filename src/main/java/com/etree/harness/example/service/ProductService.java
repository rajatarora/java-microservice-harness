package com.etree.harness.example.service;

import java.util.List;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;

/**
 * Service API that encapsulates business operations for products.
 */
public interface ProductService {

    /**
     * Create a product from the provided DTO.
     *
     * @param dto create DTO
     * @return created product response DTO
     */
    ProductResponseDto create(ProductCreateDto dto);

    /**
     * Retrieve a product by id.
     *
     * @param id product id
     * @return product response DTO
     */
    ProductResponseDto getById(Long id);

    /**
     * List all products.
     *
     * @return list of product response DTOs
     */
    List<ProductResponseDto> getAll();

    /**
     * Update a product identified by id using the provided DTO (partial or
     * full depending on implementation).
     *
     * @param id  product id
     * @param dto update DTO
     * @return updated product response DTO
     */
    ProductResponseDto update(Long id, ProductUpdateDto dto);

    /**
     * Delete a product by id.
     *
     * @param id product id
     */
    void delete(Long id);

}
