package com.kosta.farm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.User;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//	Payment findByUserId(Long userId);
//   
//	List<Payment> findAllByUser(User user);
//
//    Optional<Payment> findByOrdersIdAndUser(String ordersId, User user);

}
