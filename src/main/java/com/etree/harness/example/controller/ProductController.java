package com.etree.harness.example.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a product")
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductCreateDto dto) {
        ProductResponseDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/products/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a product")
    public ResponseEntity<ProductResponseDto> replace(@PathVariable Long id, @Valid @RequestBody ProductCreateDto dto) {
        // Treat PUT as full replace: map create DTO to update DTO
        ProductUpdateDto update = new ProductUpdateDto();
        update.setName(dto.getName());
        update.setDescription(dto.getDescription());
        update.setPrice(dto.getPrice());
        update.setStock(dto.getStock());
        ProductResponseDto updated = service.update(id, update);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a product")
    public ResponseEntity<ProductResponseDto> patch(@PathVariable Long id, @RequestBody ProductUpdateDto dto) {
        ProductResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
