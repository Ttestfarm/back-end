package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{

}
