package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
	List<Orders> findOrdersByUserId(Long userId);
}
