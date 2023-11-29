package com.kosta.farm.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class Invoice {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer invoiceId;
	// FK
	@Column
	private Integer farmerId;
	@Column
	private Integer orderId;

	@Column
	private Date invocieDate1; // 정산예정일
	@Column
	private Date invocieDate2; // 정산완료일
	@Column
	private Integer invocieCommission; // 수수료
	@Column
	private Integer invociePrice; // 가격
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String status; // 정산진행중, 정산완료, 정산오류
}
