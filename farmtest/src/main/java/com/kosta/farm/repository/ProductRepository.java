package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByProductName(String productName);
	Page<Product> findProductByFarmerId(Long FarmerId, PageRequest pageRequest);
}
