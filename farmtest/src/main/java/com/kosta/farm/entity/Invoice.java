package com.kosta.farm.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Invoice {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long invoiceId;
	// FK
	@Column
	private Long farmerId;
	@Column
	private Long orderId;
	@Column
	private Date invoiceDate1; // 정산예정일
	@Column
	private Date invoiceDate2; // 정산완료일
	@Column
	private Long invoiceCommission; // 수수료
	@Column
	private Long invoicePrice; // 가격
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String invoiceState; // 정산진행중, 정산완료, 정산오류
}
