package com.kosta.farm.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import com.kosta.farm.util.PaymentStatus;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
	@Id
	private String receiptId;
	@Column 
	private Long userId;
	@Column 
	private Long farmerId;
	@Column 
	private Long productId;
	@Column 
	private Long requestId;
	@Column 
	private Long quotationId;
	private Long deliveryId;
	
	@Column // 배송정보
	private String buyerName;
	private String buyerTel;
	private String buyerAddress;
	
	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tName; // 택배사 이름
	@Column
	private String tInvoice; // 송장번호
	
	@Column // 결제정보
	private String product; // 품목
	private Integer price; // 가격
	private Integer count; // 수량
	private String deliveryprice; // 배송비
	private String amount; // 총 결제 가격
	private String payType; // 결제 방법

	@Column
	private Date invoiceDate; // 정산예정일 "yyyy-MM-dd" 형식
	@Column
	private Integer invoiceCommission; // 수수료
	@Column
	private Integer invoicePrice; // 정산금액
	
	@Column
	private String cancelText; // 판매 취소 사유
	
	@Column
	@CreationTimestamp
	private Timestamp createDate; // 결제 완료 날짜
	@Column
	@Builder.Default
    @Enumerated(EnumType.STRING)
	private PaymentStatus state = PaymentStatus.PAID; 
	// ERROR, PAID(결제완료), FAILED, READY(대기중), CANCEL(판매취소), SHIPPING(배송중), COMPLETED(배송완료), UNSETTLEMENT(미정산), SETTLEMENT(정산완료)
}
