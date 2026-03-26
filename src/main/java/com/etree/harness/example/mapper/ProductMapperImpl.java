package com.etree.harness.example.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

@Component
public class ProductMapperImpl implements ProductMapper {

    private final ModelMapper modelMapper;

    /**
     * Create a new mapper instance.
     *
     * @param modelMapper configured ModelMapper bean
     */
    public ProductMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Convert a create DTO into a `Product` entity. Returns null when the DTO
     * is null.
     */
    @Override
    public Product toEntity(ProductCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Product.class);
    }

    /**
     * Update an existing entity from the update DTO. This performs in-place
     * mapping and is a no-op if either argument is null.
     */
    @Override
    public void updateFromDto(ProductUpdateDto dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }
        modelMapper.map(dto, entity);
    }

    /**
     * Map a `Product` entity to a response DTO. Returns null when the entity
     * is null.
     */
    @Override
    public ProductResponseDto toDto(Product entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, ProductResponseDto.class);
    }

}
