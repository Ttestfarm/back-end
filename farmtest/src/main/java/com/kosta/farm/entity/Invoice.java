package com.kosta.farm.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long invoiceId;
	// FK
	@Column
	private Long farmerId;
	@Column
	private Long orderId; // ordersId로 변경 해야 함.
	@Column
	private Date invoiceDate; // 정산예정일
	@Column
	private Integer invoiceCommission; // 수수료
	@Column
	private Integer invoicePrice; // 가격
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	@ColumnDefault("1")
	@Builder.Default
	private String invoiceState = "1"; // 정산진행중, 정산완료, 정산오류
}
