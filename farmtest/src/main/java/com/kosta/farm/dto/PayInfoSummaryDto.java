package com.kosta.farm.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.kosta.farm.util.PaymentStatus;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PayInfoSummaryDto {
	private String receiptId;
	private String ordersId;
	private Long userId;
	private Long farmerId;
	private Long productId;
	private Long quotationId; // 견적서 아이디
    private String paymentMethod; //결제 방법
	private Integer paymentDelivery; // 배송비
	private Integer productPrice; //상품가격
	private Integer count; //수량
	private String quotationQuantity; // 수량
    private BigDecimal amount; // 결제 금액
	private String productName;
	private String buyerTel;
	private String buyerName;
	private String buyerAddress;
    private Date paidAt; // 결제 완료 일시
    private Timestamp createAt;
	private PaymentStatus state;
	
	private String thumbNail; // 대표이미지 id
	private String farmName;
}
