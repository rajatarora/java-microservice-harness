package com.etree.harness.example.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing CRUD endpoints for `Product` resources.
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService service;

    /**
     * Create a new controller instance.
     *
     * @param service product service implementation
     */
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a product")
    /**
     * Create a new product.
     *
     * @param dto product create DTO
     * @return created product response with location header
     */
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductCreateDto dto) {
        log.info("Create product request received: name={} price={}", dto.getName(), dto.getPrice());
        ProductResponseDto created = service.create(dto);
        log.info("Product created: id={}", created.getId());
        return ResponseEntity.created(URI.create("/api/v1/products/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    /**
     * Retrieve a product by id.
     *
     * @param id product id
     * @return product response DTO
     */
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        log.debug("Get product by id request: id={}", id);
        ProductResponseDto resp = service.getById(id);
        log.debug("Get product by id response: id={}", resp.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @Operation(summary = "List all products")
    /**
     * List all products.
     *
     * @return list of product response DTOs
     */
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        log.debug("Get all products request");
        var list = service.getAll();
        log.debug("Get all products response: count={}", list.size());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a product")
    /**
     * Replace (full update) a product identified by id.
     *
     * @param id  product id
     * @param dto create DTO used as replacement
     * @return updated product response
     */
    public ResponseEntity<ProductResponseDto> replace(@PathVariable Long id, @Valid @RequestBody ProductCreateDto dto) {
        // Treat PUT as full replace: map create DTO to update DTO
        ProductUpdateDto update = new ProductUpdateDto();
        update.setName(dto.getName());
        update.setDescription(dto.getDescription());
        update.setPrice(dto.getPrice());
        update.setStock(dto.getStock());
        log.info("Replace product request: id={}", id);
        ProductResponseDto updated = service.update(id, update);
        log.info("Product replaced: id={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a product")
    /**
     * Partially update a product.
     *
     * @param id  product id
     * @param dto update DTO with optional fields
     * @return updated product response
     */
    public ResponseEntity<ProductResponseDto> patch(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto dto) {
        log.info("Patch product request: id={} fields={}", id, dto);
        ProductResponseDto updated = service.update(id, dto);
        log.info("Product patched: id={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    /**
     * Delete a product by id.
     *
     * @param id product id to delete
     * @return 204 No Content on success
     */
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete product request: id={}", id);
        service.delete(id);
        log.info("Product deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }

}
