package com.kosta.farm.dto;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

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
	
	// FK
	private Long userId; //주문 회원
	private Long farmerId;
	private Long requestId;
	private Long quotationId;
	private Long paymentId;
	private Long productId; //상품정보
	//상시판매를 위한 컬럼들
	private Integer ordersCount; //주문 수량
	private Integer ordersPrice; //주문 가격
	private String cancelText; // 판매 취소 사유
	private Timestamp createDate;
	private String ordersState; // 0: 판매취소, 1: 결제완료
}
