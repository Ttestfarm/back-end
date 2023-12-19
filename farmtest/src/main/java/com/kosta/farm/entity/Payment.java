package com.kosta.farm.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import com.kosta.farm.util.PaymentMethod;
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
	@Id //paymentid 대신에	
    private String receiptId; // PG 사에서 생성한 주문 번호
    //receiptId: rsp.imp_uid!!,
	@JoinColumn
	@ManyToOne
	private User user; // 구매자

    @Column(nullable = false, unique = true)
    private String ordersId; // 우리가 생성한 주문 번호 //merchant uid
    //orderId: rsp.merchant_uid

    private PaymentMethod method; // 결제 수단
    private String name; // 결제 이름
    @Column(nullable = false)
    private BigDecimal amount; // 결제 금액
    @Builder.Default
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.READY; // 상태

    @CreatedDate
    private LocalDateTime createAt; // 결제 요청 일시

    private LocalDateTime paidAt; // 결제 완료 일시

    private LocalDateTime failedAt; // 결제 실패 일시
    
    @Builder.Default
    private BigDecimal cancelledAmount = BigDecimal.ZERO; // 취소된 금액

    private LocalDateTime cancelledAt; // 결제 취소 일시

    
    //여기까지 payment을 위한 column
    
    //결제정보 테이블을 이름 똑같이 물어복;
//    apply_num
//    : 
//    ""
//    bank_name
//    : 
//    null
//    buyer_addr
//    : 
//    ""
//    buyer_email
//    : 
//    ""
//    buyer_name
//    : 
//    ""
//    buyer_postcode
//    : 
//    ""
//    buyer_tel
//    : 
//    ""
//    card_name
//    : 
//    null
//    card_number
//    : 
//    "*********"
//    card_quota
//    : 
//    0
//    currency
//    : 
//    "KRW"
//    custom_data
//    : 
//    null
//    imp_uid
//    : 
//    "imp_136605693605"
//    merchant_uid
//    : 
//    "mid_1702862604640"
//    name
//    : 
//    "사과"
//    paid_amount
//    : 
//    500
//    paid_at
//    : 
//    1702862639
//    pay_method
//    : 
//    "point"
//    pg_provider
//    : 
//    "html5_inicis"
//    pg_tid
//    : 
//    "StdpayCARDINIpayTest20231218102358979381"
//    pg_type
//    : 
//    "payment"
//    receipt_url
//    : 
//    "https://iniweb.inicis.com/DefaultWebApp/mall/cr/cm/mCmReceipt_head.jsp?noTid=StdpayCARDINIpayTest20231218102358979381&noMethod=1"
//    status
//    : 
//    "paid"
//    success
//    : 
//    true
    
	@Column
	private String paymentBank; // 결제 은행
	@Column
	private Integer paymentDelivery; // 배송비
	@Column
	private Integer paymentPrice; // 상품금액
	@Column
	@CreationTimestamp
	private Timestamp createDate; // 결제 완료 날짜
	@Column
	@ColumnDefault("1")
	private String state; // 결제취소 0, 결제완료 1
	@Column
	private Long userId; // userid 참조
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
}
