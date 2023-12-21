//package com.kosta.farm.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kosta.farm.service.PaymentService;
//import com.siot.IamportRestClient.IamportClient;
//import com.siot.IamportRestClient.response.IamportResponse;
//import com.siot.IamportRestClient.response.Payment;
//
//@RestController
//public class PaymentsApiController {
//	private final IamportClient iamportClientApi;
//	
//	//생성자로 rest api key와 secret을 입력해서 토큰 바로생성.
//	public PaymentsApiController() {
//		this.iamportClientApi = new IamportClient("6796671545054859",
//				"064a5442d844755e7f75228e97c52f81a82e80bd67136a309ba026caa2165e21bbf44deb0b6b0638");
//	}
//	@Autowired
//	private PaymentService paymentService;
//	
//	
//	public IamportResponse<Payment> paymentLookup(String impUid) throws IamportResponseException, IOException{
//		return iamportClientApi.paymentByImpUid(impUid);
//	}
//}
