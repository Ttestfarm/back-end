package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class Payment {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long paymentId;
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
	private String state; // 결제취소 0, 결제완료 1
	@Column
	private Long userId; //userid 참조
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
