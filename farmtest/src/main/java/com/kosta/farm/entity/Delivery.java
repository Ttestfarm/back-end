package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kosta.farm.util.DeliveryStatus;
import com.kosta.farm.util.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Delivery {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long deliveryId;
	// FK
	@Column(unique = true)
	private String receiptId;

	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tName; // 택배사 이름
	@Column
	private String tInvoice; // 송장번호
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	@Enumerated(EnumType.STRING)
	private String state = DeliveryStatus.SHIPPING.name();// ERROR, SHIPPING, COMPLETED
}
