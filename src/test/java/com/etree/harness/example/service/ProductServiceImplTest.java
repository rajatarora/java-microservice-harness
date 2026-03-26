package com.etree.harness.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;
import com.etree.harness.example.mapper.ProductMapper;
import com.etree.harness.example.repository.ProductRepository;
import com.etree.harness.example.service.impl.ProductServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository repository;

    @Mock
    ProductMapper mapper;

    @InjectMocks
    ProductServiceImpl service;

    // create()
    @Test
    void create_validDto_returnsResponse() {
        ProductCreateDto create = new ProductCreateDto();
        create.setName("Name");
        create.setDescription("Desc");
        create.setPrice(new BigDecimal("1.23"));
        create.setStock(5);

        Product entity = new Product();
        entity.setName("Name");
        entity.setDescription("Desc");
        entity.setPrice(new BigDecimal("1.23"));
        entity.setStock(5);

        Product saved = new Product();
        saved.setId(10L);
        saved.setName("Name");
        saved.setDescription("Desc");
        saved.setPrice(new BigDecimal("1.23"));
        saved.setStock(5);

        ProductResponseDto resp = new ProductResponseDto();
        resp.setId(10L);
        resp.setName("Name");

        when(mapper.toEntity(create)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(resp);

        ProductResponseDto out = service.create(create);

        assertNotNull(out);
        assertEquals(10L, out.getId());
        assertEquals("Name", out.getName());
        verify(mapper).toEntity(create);
        verify(repository).save(entity);
        verify(mapper).toDto(saved);
    }

    // getById()
    @Test
    void getById_existing_returnsResponse() {
        Product p = new Product();
        p.setId(1L);
        p.setName("X");

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(1L);
        dto.setName("X");

        when(repository.findById(1L)).thenReturn(Optional.of(p));
        when(mapper.toDto(p)).thenReturn(dto);

        ProductResponseDto out = service.getById(1L);
        assertNotNull(out);
        assertEquals(1L, out.getId());
        verify(repository).findById(1L);
        verify(mapper).toDto(p);
    }

    @Test
    void getById_notFound_throws() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getById(2L));
        assertTrue(ex.getMessage().contains("2"));
    }

    @Test
    void getById_nullId_throws() {
        when(repository.findById(null)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getById(null));
        assertTrue(ex.getMessage().contains("null"));
    }

    // getAll()
    @Test
    void getAll_empty_returnsEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<ProductResponseDto> out = service.getAll();
        assertNotNull(out);
        assertTrue(out.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getAll_nonEmpty_mapsAll() {
        Product a = new Product();
        a.setId(1L);
        Product b = new Product();
        b.setId(2L);

        ProductResponseDto da = new ProductResponseDto();
        da.setId(1L);
        ProductResponseDto db = new ProductResponseDto();
        db.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(a, b));
        when(mapper.toDto(a)).thenReturn(da);
        when(mapper.toDto(b)).thenReturn(db);

        List<ProductResponseDto> out = service.getAll();
        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
    }

    // update()
    @Test
    void update_existing_updatesAndReturns() {
        Product existing = new Product();
        existing.setId(5L);
        existing.setName("Old");

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("New");

        Product saved = new Product();
        saved.setId(5L);
        saved.setName("New");

        ProductResponseDto outDto = new ProductResponseDto();
        outDto.setId(5L);
        outDto.setName("New");

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        // mapper.updateFromDto does in-place mapping; we just verify it's invoked
        doAnswer(i -> { existing.setName(dto.getName()); return null; }).when(mapper).updateFromDto(dto, existing);
        when(repository.save(existing)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(outDto);

        ProductResponseDto result = service.update(5L, dto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("New", result.getName());
        verify(mapper).updateFromDto(dto, existing);
        verify(repository).save(existing);
        verify(mapper).toDto(saved);
    }

    @Test
    void update_nullDto_callsMapperAndReturns() {
        Product existing = new Product();
        existing.setId(7L);
        existing.setName("Keep");

        Product saved = new Product();
        saved.setId(7L);
        saved.setName("Keep");

        ProductResponseDto outDto = new ProductResponseDto();
        outDto.setId(7L);
        outDto.setName("Keep");

        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        // mapper.updateFromDto should be called with null and do nothing
        doNothing().when(mapper).updateFromDto(null, existing);
        when(repository.save(existing)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(outDto);

        ProductResponseDto result = service.update(7L, null);

        assertNotNull(result);
        assertEquals(7L, result.getId());
        verify(mapper).updateFromDto(null, existing);
        verify(repository).save(existing);
    }

    @Test
    void update_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.update(99L, new ProductUpdateDto()));
        assertTrue(ex.getMessage().contains("99"));
    }

    // delete()
    @Test
    void delete_existing_deletes() {
        when(repository.existsById(3L)).thenReturn(true);
        doNothing().when(repository).deleteById(3L);

        service.delete(3L);

        verify(repository).existsById(3L);
        verify(repository).deleteById(3L);
    }

    @Test
    void delete_notFound_throws() {
        when(repository.existsById(4L)).thenReturn(false);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(4L));
        assertTrue(ex.getMessage().contains("4"));
    }

    @Test
    void delete_nullId_throws() {
        when(repository.existsById(null)).thenReturn(false);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(null));
        assertTrue(ex.getMessage().contains("null"));
    }

}
