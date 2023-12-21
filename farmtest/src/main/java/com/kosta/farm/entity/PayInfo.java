package com.kosta.farm.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayInfo {
	@Id //paymentid 대신에	
    private String receiptId; // PG 사에서 생성한 주문 번호
    //receiptId: rsp.imp_uid!!,
    @Column(nullable = true, unique = true)
    private String ordersId; // 우리가 생성한 주문 번호 //merchant uid //orderId: rsp.merchant_uid
    @Column
    private Long userId; // userId 참조
    private String paymentMethod; //결제 방법
    @Column(nullable = true)
    private BigDecimal amount; // 결제 금액
    @Column(nullable = true)
    private String status;
    @CreationTimestamp
    private LocalDateTime createAt;
    private Date paidAt; // 결제 완료 일시
    private String pgType;
    private String pgTid;
    private LocalDateTime failedAt; // 결제 실패 일시
    
    @Builder.Default
    private BigDecimal cancelledAmount = BigDecimal.ZERO; // 취소된 금액

    private LocalDateTime cancelledAt; // 결제 취소 일시

	@Column
	private Integer paymentDelivery; // 배송비
	@Column
	private Integer productPrice; //상품가격
	@Column
	private Integer count; //수량
	@Column
	private Long farmerId;
	@Column
	private Long requestId; //요청서 아이디
	@Column
	private Long quotationId; //견적서 아이디
    
	@Column //프론트에서 받아오는 수령인 및 제품 정보
	private Long productId;
	private String productName;
	private String buyerTel;
	private String buyerName;
	private String buyerAddress;

	
	
}