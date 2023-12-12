package com.kosta.farm.dto;

import java.security.Timestamp;

import lombok.Data;

@Data
public class OrdersDto {
	private Long orderId; // 주문번호
	private Timestamp date;	//결제 완료 시간
	// 배송 정보
	private String product; // 품목
	private String quantity; // 수량
	private String name; // 수령인
	private String tel; // 연락처
	private String address; // 배송 주소

	// 결제 정보
	private String paymentBank;
	private Integer price; // 금액
	private Integer delivery; // 배송비
	
	
}
