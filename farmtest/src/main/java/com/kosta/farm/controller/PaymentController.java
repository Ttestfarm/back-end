//package com.kosta.farm.controller;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kosta.farm.entity.PayInfo;
//import com.kosta.farm.entity.User;
//import com.kosta.farm.service.FarmService;
//import com.kosta.farm.service.PaymentService;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//@RestController
//public class PaymentController {
//	@Autowired
//	private PaymentService paymentService;
//	@Autowired
//	private FarmService farmService;
//	
//	
//	// 카드 결제 성공 후
//	@PostMapping("/processPayment")
//	public ResponseEntity<String> processPayment(
//			Authentication authentication, 
//			@RequestBody PayInfo payInfo)
//			throws IOException {
//		User user = (User) authentication.getPrincipal();
//		System.out.println(user);
//		Long userId = user.getUserId();
//
//		String token = paymentService.getToken();
//
//		System.out.println("토큰 : " + token);
//		System.out.println(payInfo);
//		// 결제 완료된 금액
//		try {
//			Boolean paySuccess = paymentService.paymentInfo(payInfo.getReceiptId(), token, payInfo);
//			System.out.println(payInfo);
//			payInfo.setUserId(userId);
//			if(paySuccess) farmService.savePaymentInfo(payInfo);
//			return new ResponseEntity<>("주문이 완료되었습니다", HttpStatus.OK);
//
//		} catch (Exception e) {
//			e.printStackTrace();
////			paymentService.payMentCancel(token, orderInfo.getImpUid(), amount, "결제 에러");
//			return new ResponseEntity<String>("결제 에러", HttpStatus.BAD_REQUEST);
//		}
//
//	}
//}
