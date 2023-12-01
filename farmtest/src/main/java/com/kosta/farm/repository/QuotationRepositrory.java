package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Quotation;

public interface QuotationRepositrory extends JpaRepository<Quotation, Integer> {

	
}
