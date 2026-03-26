package com.etree.harness.example.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.etree.harness.TestcontainersConfiguration;
import com.etree.harness.example.entity.Product;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createsProduct_savesAndReturnsWithId() {
        Product p = new Product("Test Product", "A product for testing", new BigDecimal("9.99"), 5);

        Product saved = productRepository.save(p);

        assertNotNull(saved.getId(), "Saved product should have an ID");
        assertEquals("Test Product", saved.getName());
        assertEquals(0, saved.getPrice().compareTo(new BigDecimal("9.99")));
        assertEquals(5, saved.getStock());
    }

    @Test
    void findById_returnsProduct() {
        Product p = new Product("FindMe", "find by id", new BigDecimal("1.00"), 2);
        Product persisted = productRepository.saveAndFlush(p);

        Optional<Product> found = productRepository.findById(persisted.getId());

        assertTrue(found.isPresent());
        assertEquals("FindMe", found.get().getName());
    }

    @Test
    void findAll_returnsAllProducts() {
        Product p1 = new Product("P1", "one", new BigDecimal("1.00"), 1);
        Product p2 = new Product("P2", "two", new BigDecimal("2.00"), 2);

        productRepository.saveAndFlush(p1);
        productRepository.saveAndFlush(p2);

        List<Product> all = productRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void updateProduct_changesPersistedValues() {
        Product p = new Product("OldName", "desc", new BigDecimal("3.00"), 3);
        Product saved = productRepository.save(p);

        saved.setName("NewName");
        productRepository.saveAndFlush(saved);

        Optional<Product> updated = productRepository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("NewName", updated.get().getName());
    }

    @Test
    void deleteById_removesProduct() {
        Product p = new Product("ToDelete", "desc", new BigDecimal("4.00"), 4);
        Product saved = productRepository.save(p);

        productRepository.deleteById(saved.getId());

        assertFalse(productRepository.existsById(saved.getId()));
    }

    @Test
    void countAndExistsBehaviors() {
        productRepository.deleteAll();

        assertEquals(0, productRepository.count());

        Product p = new Product("C1", "desc", new BigDecimal("5.00"), 1);
        Product saved = productRepository.save(p);

        assertEquals(1, productRepository.count());
        assertTrue(productRepository.existsById(saved.getId()));
    }

    @Test
    void constraintViolation_throwsException() {
        // name is non-nullable; saving without name should fail on flush
        Product invalid = new Product(null, "no name", new BigDecimal("1.00"), 1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepository.saveAndFlush(invalid);
        });
    }
}
