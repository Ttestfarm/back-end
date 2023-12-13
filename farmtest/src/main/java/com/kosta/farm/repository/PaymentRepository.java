package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByUserId(Long userId);
}
