package com.kosta.farm.service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.PaymentRepository;
import com.kosta.farm.util.PaymentMethod;
import com.kosta.farm.util.PaymentStatus;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;

@Service
public class PaymentService {
//iamport API 키와 시크릿을 주입받는 부분
	private final PaymentRepository paymentRepository;
	@Value("${iamport.key}")
	private String restApiKey;
	@Value("${iamport.secret}")
	private String restApiSecret;
	private IamportClient iamportClient;
	@Autowired
	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	@Transactional //사용자의 결제 정보를 생성하는 메서드
	public Payment requestPayment(User user, String name, BigDecimal amount) throws Exception {
		Payment payment = new Payment()	;
		payment.setUser(user);
		payment.setOrdersId(user.getUserName() + "_" + Objects.hash(user, name, amount, System.currentTimeMillis()));
		payment.setName(name);
		payment.setAmount(amount);
		return paymentRepository.save(payment);
	}
	@Transactional
	public Payment verifyPayment(Payment payment, User user) throws Exception {
        // 결제 내역의 유효성을 확인하고 결제 상태를 업데이트
		if (!payment.getUser().equals(user)) {
			throw new NotFoundException();
		}
		IamportClient iamportClient = new IamportClient(restApiKey, restApiSecret);
		try {
			IamportResponse<com.siot.IamportRestClient.response.Payment> paymentResponse = iamportClient
					.paymentByImpUid(payment.getReceiptId());
			if (Objects.nonNull(paymentResponse.getResponse())) {
				com.siot.IamportRestClient.response.Payment paymentData = paymentResponse.getResponse();
				if (payment.getReceiptId().equals(paymentData.getImpUid())
						&& payment.getOrdersId().equals(paymentData.getMerchantUid())
						&& payment.getAmount().compareTo(paymentData.getAmount()) == 0) {
					PaymentMethod method = PaymentMethod.valueOf(paymentData.getPayMethod().toUpperCase());
					PaymentStatus status = PaymentStatus.valueOf(paymentData.getStatus().toUpperCase());
					payment.setMethod(method);
					payment.setStatus(status);
					paymentRepository.save(payment);
					if (status.equals(PaymentStatus.READY)) {
						if (method.equals(PaymentMethod.VBANK)) {
							throw new Exception(paymentData.getVbankNum() + " " + paymentData.getVbankDate() + " "
									+ paymentData.getVbankName());
						} else {
							throw new Exception("Payment was not completed.");
						}
					} else if (status.equals(PaymentStatus.PAID)) {
						payment.setPaidAt(
								paymentData.getPaidAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
						paymentRepository.save(payment);
					} else if (status.equals(PaymentStatus.FAILED)) {
						throw new Exception("Payment failed.");
					} else if (status.equals(PaymentStatus.CANCELLED)) {
						throw new Exception("This is a cancelled payment.");
					}
				} else {
					throw new Exception("The amount paid and the amount to be paid do not match.");
				}
			} else {
				throw new NotFoundException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payment;
	}
	
    // receiptId와 ordersId를 기준으로 결제 내역을 확인하고 결제 상태를 업데이트하는 메서드
	@Transactional
	public Payment verifyPayment(String receiptId, String ordersId, User user) throws Exception {
		Optional<Payment> oPayment = paymentRepository.findByOrdersIdAndUser(ordersId, user);
		if (oPayment.isPresent()) {
			Payment payment = oPayment.get();
			payment.setReceiptId(receiptId);
			return verifyPayment(payment, user);
		} else {
			throw new Exception(ordersId + "결제정보를 찾지 못했습니다");
		}
	}
}
