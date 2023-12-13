package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@DynamicInsert
@DynamicUpdate
public class Delivery {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long deliveryId;
	// FK
	@Column
	private Long ordersId;
	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tInvoice; // 송장번호
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	@ColumnDefault("1")
	private String deliveryState; // 0: 오류, 1: 배송중, 2: 배송 완료
	
}
