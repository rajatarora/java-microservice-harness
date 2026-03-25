package com.etree.harness.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;
import com.etree.harness.example.mapper.ProductMapper;
import com.etree.harness.example.repository.ProductRepository;
import com.etree.harness.example.service.ProductService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProductResponseDto create(ProductCreateDto dto) {
        Product entity = mapper.toEntity(dto);
        Product saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {
        Product p = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return mapper.toDto(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto update(Long id, ProductUpdateDto dto) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        mapper.updateFromDto(dto, existing);
        Product saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
    }

}
