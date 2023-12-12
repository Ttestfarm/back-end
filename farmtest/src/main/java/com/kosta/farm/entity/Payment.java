package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
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
	@ColumnDefault("1")
	private String state; // 결제취소 0, 결제완료 1
	
}
