package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@Column 
	private Long deliveryId;
	
	@Column // 배송정보
	private String buyerName;
	private String buyerTel;
	private String buyerAddress;
	
	@Column // 결제정보
	private String product; // 품목
	private String price; // 가격
	private String deliveryprice; // 배송비
	private String amount; // 총 결제 가격
	private String payType; // 결제 방법

	@Column
	@CreationTimestamp
	private Timestamp createDate; // 결제 완료 날짜
	@Column
    @Enumerated(EnumType.STRING)
	private String state = PaymentStatus.PAID.name(); // READY, PAID, FAILED, CANCELLED
}
