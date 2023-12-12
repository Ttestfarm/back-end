package com.kosta.farm.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class OrdersDto {
	private Long ordersId; // 주문번호
	private String date;	//결제 완료 시간
	// 배송 정보
	private String product; // 품목
	private String quantity; // 수량
	private String name; // 수령인
	private String tel; // 연락처
	private String address; // 배송 주소
	private Integer price; // 금액

	// 결제 정보
	private Integer paymentPrice; // 결제 금액
	private String paymentBank; // 결제수단
	private Integer delivery; // 배송비
	private String paymentState; // 결제 현황
}
