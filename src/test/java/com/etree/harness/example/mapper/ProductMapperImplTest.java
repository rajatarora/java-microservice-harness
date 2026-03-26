package com.etree.harness.example.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.entity.Product;

class ProductMapperImplTest {

    private ProductMapperImpl mapper;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        Condition<Object, Object> isNotNull = context -> context.getSource() != null;
        modelMapper.getConfiguration().setPropertyCondition(isNotNull);
        mapper = new ProductMapperImpl(modelMapper);
    }

    // toEntity
    @Test
    void toEntity_nullDto_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_allFields_mapped() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Widget");
        dto.setDescription("A useful widget");
        dto.setPrice(new BigDecimal("9.99"));
        dto.setStock(5);

        Product entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("Widget", entity.getName());
        assertEquals("A useful widget", entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("9.99")));
        assertEquals(5, entity.getStock());
    }

    @Test
    void toEntity_nullDescription_allowed() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("NoDesc");
        dto.setDescription(null);
        dto.setPrice(new BigDecimal("1.00"));
        dto.setStock(0);

        Product entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("NoDesc", entity.getName());
        assertNull(entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("1.00")));
        assertEquals(0, entity.getStock());
    }

    // updateFromDto
    @Test
    void updateFromDto_nullDto_entityUnchanged() {
        Product entity = new Product();
        entity.setName("Old");
        entity.setDescription("OldDesc");
        entity.setPrice(new BigDecimal("2.00"));
        entity.setStock(10);

        mapper.updateFromDto(null, entity);

        assertEquals("Old", entity.getName());
        assertEquals("OldDesc", entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("2.00")));
        assertEquals(10, entity.getStock());
    }

    @Test
    void updateFromDto_nullEntity_noException() {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("NewName");
        dto.setDescription("NewDesc");
        dto.setPrice(new BigDecimal("3.00"));
        dto.setStock(7);

        assertDoesNotThrow(() -> mapper.updateFromDto(dto, null));
    }

    @Test
    void updateFromDto_allFields_overwritesEntity() {
        Product entity = new Product();
        entity.setName("Old");
        entity.setDescription("OldDesc");
        entity.setPrice(new BigDecimal("2.00"));
        entity.setStock(10);

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("New");
        dto.setDescription("NewDesc");
        dto.setPrice(new BigDecimal("5.50"));
        dto.setStock(3);

        mapper.updateFromDto(dto, entity);

        assertEquals("New", entity.getName());
        assertEquals("NewDesc", entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("5.50")));
        assertEquals(3, entity.getStock());
    }

    @Test
    void updateFromDto_allDtoFieldsNull_preservesEntityFields() {
        Product entity = new Product();
        entity.setName("KeepName");
        entity.setDescription("KeepDesc");
        entity.setPrice(new BigDecimal("10.00"));
        entity.setStock(99);

        ProductUpdateDto dto = new ProductUpdateDto(); // all nulls

        mapper.updateFromDto(dto, entity);

        assertEquals("KeepName", entity.getName());
        assertEquals("KeepDesc", entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("10.00")));
        assertEquals(99, entity.getStock());
    }

    @Test
    void updateFromDto_partialDto_onlyUpdatesProvidedFields() {
        Product entity = new Product();
        entity.setName("OldName");
        entity.setDescription("OldDesc");
        entity.setPrice(new BigDecimal("4.00"));
        entity.setStock(20);

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("OnlyName"); // other fields null

        mapper.updateFromDto(dto, entity);

        assertEquals("OnlyName", entity.getName());
        assertEquals("OldDesc", entity.getDescription());
        assertEquals(0, entity.getPrice().compareTo(new BigDecimal("4.00")));
        assertEquals(20, entity.getStock());
    }

    // toDto
    @Test
    void toDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toDto_allFields_mappedToResponseDto() {
        Product entity = new Product();
        entity.setId(123L);
        entity.setName("RespName");
        entity.setDescription("RespDesc");
        entity.setPrice(new BigDecimal("15.75"));
        entity.setStock(42);
        entity.setCreatedAt(LocalDateTime.of(2020, 1, 2, 3, 4));
        entity.setUpdatedAt(LocalDateTime.of(2021, 2, 3, 4, 5));
        entity.setCreatedBy("creator");
        entity.setUpdatedBy("updater");

        ProductResponseDto dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(123L, dto.getId());
        assertEquals("RespName", dto.getName());
        assertEquals("RespDesc", dto.getDescription());
        assertEquals(0, dto.getPrice().compareTo(new BigDecimal("15.75")));
        assertEquals(42, dto.getStock());
        assertEquals(LocalDateTime.of(2020, 1, 2, 3, 4), dto.getCreatedAt());
        assertEquals(LocalDateTime.of(2021, 2, 3, 4, 5), dto.getUpdatedAt());
        assertEquals("creator", dto.getCreatedBy());
        assertEquals("updater", dto.getUpdatedBy());
    }

    @Test
    void toDto_optionalFieldsNull_ok() {
        Product entity = new Product();
        entity.setName("OnlyRequired");
        entity.setPrice(new BigDecimal("0.00"));
        entity.setStock(0);

        ProductResponseDto dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals("OnlyRequired", dto.getName());
        assertNull(dto.getDescription());
        assertEquals(0, dto.getPrice().compareTo(new BigDecimal("0.00")));
        assertEquals(0, dto.getStock());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getUpdatedBy());
    }

}
