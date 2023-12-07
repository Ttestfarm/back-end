package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.ProductFile;

public interface ProductFileRepository extends JpaRepository<ProductFile, Long> {

}
