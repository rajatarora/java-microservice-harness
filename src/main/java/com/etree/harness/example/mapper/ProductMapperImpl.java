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

    public ProductMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Product toEntity(ProductCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Product.class);
    }

    @Override
    public void updateFromDto(ProductUpdateDto dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }
        modelMapper.map(dto, entity);
    }

    @Override
    public ProductResponseDto toDto(Product entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, ProductResponseDto.class);
    }

}
