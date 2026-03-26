package com.etree.harness.example.mapper;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

/**
 * Mapper interface for converting between product DTOs and the `Product`
 * entity.
 */
public interface ProductMapper {

    /**
     * Map a create DTO to an entity.
     *
     * @param dto create DTO
     * @return mapped entity or null
     */
    Product toEntity(ProductCreateDto dto);

    /**
     * Update an existing entity with values from the update DTO.
     *
     * @param dto    update DTO (may be null)
     * @param entity target entity to update
     */
    void updateFromDto(ProductUpdateDto dto, Product entity);

    /**
     * Map an entity to a response DTO.
     *
     * @param entity product entity
     * @return response DTO or null
     */
    ProductResponseDto toDto(Product entity);

}
