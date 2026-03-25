package com.etree.harness.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.etree.harness.example.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
