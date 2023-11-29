package com.kosta.farm.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Delivery {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer deliveryId;
	// FK
	@Column
	private Integer orderId;
	
	@Column 
	private String tCode; // 택배사 코드
	@Column
	private String tInvocie; // 송장번호
	@Column
	private Date createDate;
	@Column
	private String state;
	
}
