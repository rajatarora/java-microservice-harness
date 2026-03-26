package com.etree.harness.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;
import com.etree.harness.example.mapper.ProductMapper;
import com.etree.harness.example.repository.ProductRepository;
import com.etree.harness.example.service.ProductService;

import jakarta.persistence.EntityNotFoundException;

/**
 * Default `ProductService` implementation containing business logic for
 * creating, retrieving, updating and deleting products.
 */
@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    /**
     * Construct the service with required dependencies.
     *
     * @param repository product repository
     * @param mapper     product mapper
     */
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductResponseDto create(ProductCreateDto dto) {
        log.debug("Creating product: name={} price={}", dto.getName(), dto.getPrice());
        Product entity = mapper.toEntity(dto);
        Product saved = repository.save(entity);
        log.info("Product saved: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {
        Product p = repository.findById(id).orElseThrow(() -> {
            log.warn("Product not found: id={}", id);
            return new EntityNotFoundException("Product not found: " + id);
        });
        return mapper.toDto(p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        var list = repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
        log.debug("Retrieved products: count={}", list.size());
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductResponseDto update(Long id, ProductUpdateDto dto) {
        Product existing = repository.findById(id).orElseThrow(() -> {
            log.warn("Product not found for update: id={}", id);
            return new EntityNotFoundException("Product not found: " + id);
        });
        log.debug("Updating product: id={}", id);
        mapper.updateFromDto(dto, existing);
        Product saved = repository.save(existing);
        log.info("Product updated: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Delete requested for non-existent product: id={}", id);
            throw new EntityNotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted product: id={}", id);
    }

}
