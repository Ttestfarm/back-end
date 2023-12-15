//package com.kosta.farm.controller;
//
//import java.math.BigDecimal;
//
//import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kosta.farm.dto.PaymentRequest;
//import com.kosta.farm.entity.Payment;
//import com.kosta.farm.entity.User;
//import com.kosta.farm.service.PaymentService;
//import com.kosta.farm.service.UserService;
//
//import lombok.RequiredArgsConstructor;
//
//@RequestMapping("payments")
//@RequiredArgsConstructor
//@RestController
//public class PaymentController {
//	private final PaymentService paymentService;
//	private final UserService userService;
//
//	@PostMapping
//	public ResponseEntity<?> requestPayment(Authentication authentication, @RequestBody PaymentRequest request)
//			throws Exception {
//		User user = (User) authentication.getPrincipal();
//		BigDecimal amount = new BigDecimal(request.getAmount());
//		Payment payment = paymentService.requestPayment(user, request.getName(), amount);
//		return ResponseEntity.ok(payment);
//	}
//
//	@PutMapping("{ordersId}")
//	public ResponseEntity<?> verifyPayment(Authentication authentication, @PathVariable String ordersId,
//			@RequestBody String receiptId) throws Exception {
//		User user = (User) authentication.getPrincipal();
////		Long userId = user.getUserId();
//
//		Payment payment = paymentService.verifyPayment(receiptId, ordersId, user);
//		return ResponseEntity.ok(payment);
//	}
//
//}
