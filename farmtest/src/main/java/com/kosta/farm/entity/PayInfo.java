package com.kosta.farm.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

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
public class PayInfo {
	@Id //paymentid 대신에	
    private String receiptId; // PG 사에서 생성한 주문 번호
    //receiptId: rsp.imp_uid!!,
    @Column(nullable = true, unique = true)
    private String ordersId; // 우리가 생성한 주문 번호 //merchant uid //orderId: rsp.merchant_uid
    @Column
    private Long userId; // userId 참조
    @Column
    private Long farmerId;
    @Column
    private Long productId;
	@Column
	private Long requestId; //요청서 아이디
	@Column
	private Long quotationId; //견적서 아이디
    
	//프론트에서 받아오는 수령인 및 제품 정보
    @Column
    private String paymentMethod; //결제 방법
    @Column(nullable = true)
    private BigDecimal amount; // 결제 금액
    @Column(nullable = true)
    private String status;
    @Builder.Default
    private BigDecimal cancelledAmount = BigDecimal.ZERO; // 취소된 금액
    private LocalDateTime cancelledAt; // 결제 취소 일시
    
    @Column
	private Integer paymentDelivery; // 배송비
	@Column
	private Integer productPrice; //상품가격
	
	// 택배
	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tName; // 택배사 이름
	@Column
	private String tInvoice; // 송장번호
    
	@Column //프론트에서 받아오는 수령인 및 제품 정보
	private Integer count; //수량
	private String productName;
	private String buyerTel;
	private String buyerName;
	private String buyerAddress;

    @CreationTimestamp
    private LocalDateTime createAt;
    private Date paidAt; // 결제 완료 일시
    private String pgType;
    private String pgTid;
    private LocalDateTime failedAt; // 결제 실패 일시
	
	// 정산
	@Column
	private Date invoiceDate; // 정산예정일 "yyyy-MM-dd" 형식
	@Column
	private Integer invoiceCommission; // 수수료
	@Column
	private String invoicePrice; // 정산금액
	
	// 판매 취소
	@Column
	private String cancelText; // 판매 취소 사유
	
	@Column
	@Builder.Default
    @Enumerated(EnumType.STRING)
	private PaymentStatus state = PaymentStatus.PAID; 
	// ERROR, PAID(결제완료), FAILED, READY(대기중), CANCEL(판매취소), SHIPPING(배송중), COMPLETED(배송완료), UNSETTLEMENT(미정산), SETTLEMENT(정산완료)

}
