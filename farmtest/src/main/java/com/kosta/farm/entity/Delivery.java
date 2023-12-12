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
	private Long ordersId; // ordersId로 수정
	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tInvocie; // 송장번호
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	@ColumnDefault("0")
	private String deliveryState; // 배송중, 배송 완료
	
}
