package com.kosta.farm.service;

import java.math.BigDecimal;

import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.User;

public interface PaymentService {
	Payment requestPayment(User user, String name, BigDecimal amount) throws Exception;

	Payment verifyPayment(Payment payment, User user) throws Exception;

	Payment verifyPayment(String receiptId, String orderId, User user) throws Exception;
}
