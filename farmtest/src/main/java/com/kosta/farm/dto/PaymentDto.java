package com.kosta.farm.dto;

import java.sql.Date;

import javax.persistence.Column;

import com.kosta.farm.util.PaymentStatus;

import lombok.Data;

@Data
public class PaymentDto {
	private String receiptId; // 결제번호
	private Long userId;
	private Long farmerId;
	private Long productId;
	private Long requestId;
	private Long quotationId;
	private Long deliveryId;

	private String buyerName;
	private String buyerTel;
	private String buyerAddress;

	private String tCode; // 택배사 코드
	private String tName; // 택배사 이름
	private String tInvoice; // 송장번호
	
	private String product; // 품목
	private Integer price; // 가격
	private Integer count; // 수량
	
	private String deliveryprice; // 배송비
	private String amount; // 총 결제 금액
	private String payType; // 결제 방법
	
	private Date invoiceDate; // 정산예정일
	private Integer invoiceCommission; // 수수료
	private Integer invoicePrice; // 정산금액

	private String cancelText; // 판매 취소 사유
	
	private String createDate; // 결제 완료 날짜
	private PaymentStatus state; // ERROR, PAID, FAILED, READY, SHIPPING, COMPLETED

	
}
