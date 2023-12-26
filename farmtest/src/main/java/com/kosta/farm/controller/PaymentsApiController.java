package com.kosta.farm.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.PayService;
import com.kosta.farm.service.RefundService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그 선언
@RequiredArgsConstructor
@RestController
public class PaymentsApiController {
	private IamportClient iamportClient;
	private final PayService payService;
	private final RefundService refundService;
	@Value("${imp_key}")
	private String impKey;

	@Value("${imp_secret}")
	private String impSecret;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(impKey, impSecret);
	}

	@PostMapping("/payment")
	public ResponseEntity<String> paymentComplete(Authentication authentication, @RequestBody PayInfo payInfo)
			throws Exception {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		String receiptId = payInfo.getReceiptId();
		try {
			System.out.println(userId);
			payInfo.setUserId(userId);
			payService.savepayInfo(payInfo);
			log.info("결제 성공 :주문번호 {}", receiptId);
			return new ResponseEntity<String>("결제 성공", HttpStatus.OK);
		} catch (Exception e) {
			log.info("주문 상품 환불 진행 : 주문 번호 {}", receiptId);
			String token = refundService.getToken(impKey, impSecret);
			refundService.refundRequest(token, receiptId, e.getMessage());
			
			e.printStackTrace();
			return new ResponseEntity<String>("결제 에러", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/payment/validation/{imp_uid}")
	@ResponseBody
	public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid)
			throws IamportResponseException, IOException {
		IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
		log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse().getMerchantUid());
		return payment;
	}

	@PostMapping("/refund/{recieptId}") //취소요청
	public ResponseEntity<String> refundPayment(@PathVariable String recieptId) {
		try {
			String reason="재고";
			String token = refundService.getToken(impKey, impSecret);
			refundService.refundRequest(token, recieptId, reason);
			return ResponseEntity.ok("결제 취소 요청이 성공적으로 전송되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 취소 요청에 실패하였습니다.");
		}
	}

}
