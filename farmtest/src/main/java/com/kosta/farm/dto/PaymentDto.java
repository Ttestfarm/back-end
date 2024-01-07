package com.kosta.farm.dto;

import java.math.BigDecimal;
import java.sql.Date;

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
	private String quotationQuantity; // 수량

	private String buyerName;
	private String buyerTel;
	private String buyerAddress;

	private String tCode; // 택배사 코드
	private String tName; // 택배사 이름
	private String tInvoice; // 송장번호
	
	private String productName; // 품목
	private Integer productPrice; // 가격
	private Integer count; // 수량
	
	private Integer paymentDelivery; // 배송비
	private BigDecimal amount; // 총 결제 금액
	private String pgType; // 결제 방법
	
	private Date invoiceDate; // 정산예정일
	private Integer invoiceCommission; // 수수료
	private String invoicePrice; // 정산금액

	private String cancelText; // 판매 취소 사유
	
	private String paidAt; // 결제 완료 날짜
	private PaymentStatus state; // ERROR, PAID, FAILED, READY, SHIPPING, COMPLETED

	
}
