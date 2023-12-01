package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByProductName(String productName);
}
