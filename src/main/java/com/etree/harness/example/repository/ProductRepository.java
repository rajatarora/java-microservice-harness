package com.etree.harness.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.etree.harness.example.entity.Product;

/**
 * Spring Data repository for {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

}
