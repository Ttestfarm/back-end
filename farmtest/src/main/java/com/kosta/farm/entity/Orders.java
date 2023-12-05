package com.kosta.farm.entity;


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
public class Orders { //order> orders로 대체 entity 생성 오류 때문에
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ordersId;
	// FK
	@Column
	private Long userId;
	@Column
	private Long farmerId;
	@Column
	private Long requestId;
	@Column
	private Long productId;
	@Column
	private Long quotationId;
	@Column
	private Long paymentId;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String ordersState; // 결제완료 0, 판매취소 1, 배송완료 2
	
	
}
