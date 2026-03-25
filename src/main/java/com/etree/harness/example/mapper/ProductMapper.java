package com.etree.harness.example.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    Product toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProductUpdateDto dto, @MappingTarget Product entity);

    ProductResponseDto toDto(Product entity);

}
