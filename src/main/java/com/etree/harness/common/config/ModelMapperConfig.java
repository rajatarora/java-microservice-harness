package com.etree.harness.common.config;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Condition<Object, Object> isNotNull = context -> context.getSource() != null;
        modelMapper.getConfiguration().setPropertyCondition(isNotNull);

        return modelMapper;
    }

}
