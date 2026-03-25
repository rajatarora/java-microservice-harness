package com.etree.harness.common.config;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Condition<Object, Object> isNotNull = context -> context.getSource() != null;
        modelMapper.getConfiguration().setPropertyCondition(isNotNull);

        TypeMap<ProductCreateDto, Product> createMap = modelMapper.createTypeMap(ProductCreateDto.class, Product.class);
        createMap.addMappings(m -> {
            m.skip(Product::setId);
            m.skip(Product::setCreatedAt);
            m.skip(Product::setUpdatedAt);
            m.skip(Product::setCreatedBy);
            m.skip(Product::setUpdatedBy);
        });

        TypeMap<ProductUpdateDto, Product> updateMap = modelMapper.createTypeMap(ProductUpdateDto.class, Product.class);
        updateMap.addMappings(m -> {
            m.skip(Product::setId);
            m.skip(Product::setCreatedAt);
            m.skip(Product::setUpdatedAt);
            m.skip(Product::setCreatedBy);
            m.skip(Product::setUpdatedBy);
        });

        return modelMapper;
    }

}
