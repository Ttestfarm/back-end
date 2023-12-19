package com.kosta.farm.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.PaymentRequest;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.PaymentService;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("payments")
@RequiredArgsConstructor
@RestController
public class PaymentController {
	private final PaymentService paymentService;
	private final UserService userService;

//	@Value("${iamport.key}")
//	private String restApiKey;
//	@Value("${iamport.secret}")
//	private String restApiSecret;
//
//	private IamportClient iamportClient;
//
//	@PostConstruct
//	public void init() {
//		this.iamportClient = new IamportClient(restApiKey, restApiSecret);
//	}
//    @PostMapping("/verifyIamport/{imp_uid}")
//    public IamportResponse<com.siot.IamportRestClient.response.Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
//        return iamportClient.paymentByImpUid(imp_uid);
//    }

	@PostMapping //결제요청
	public ResponseEntity<?> requestPayment(Authentication authentication, @RequestBody PaymentRequest request)
			throws Exception {
		User user = (User) authentication.getPrincipal();
		BigDecimal amount = new BigDecimal(request.getAmount());
		Payment payment = paymentService.requestPayment(user, request.getName(), amount);
		return ResponseEntity.ok(payment);
	}

	@PutMapping("{orderId}") //결제검증
	public ResponseEntity<?> verifyPayment
	(Authentication authentication, @PathVariable String ordersId,
		@RequestBody String receiptId) throws Exception {
		User user = (User) authentication.getPrincipal();
		Payment payment = paymentService.verifyPayment(receiptId, ordersId, user);
		return ResponseEntity.ok(payment);
	}

//	@ResponseBody
//	@RequestMapping(value = "/verifyIamport/{imp_uid}")
//	public IamportResponse<com.siot.IamportRestClient.response.Payment> paymentByImpUid(Model model, Locale locale,
//			HttpSession session, @PathVariable(value = "imp_uid") String imp_uid)
//			throws IamportResponseException, IOException {
//		return iamportClient.paymentByImpUid(imp_uid);
//	}

}
