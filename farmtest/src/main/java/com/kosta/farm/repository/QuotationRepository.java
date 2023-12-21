package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Quotation;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
	//requestid별로 quote목록 보기
	List<Quotation> findQuoteByRequestId(Long requestId);
	
}
