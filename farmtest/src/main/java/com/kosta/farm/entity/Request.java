package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Request {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long requestId;
	@Column
	private Long userId;
	@Column
	private String requestProduct; //요청 품목
	@Column
	private String requestQuantity; //요청 물량
	@Column
	private String requestDate; // 요청 날짜
	@Column
	private String requestMessage; //요청메세지
	@Column
	private Boolean choiceState; // 0 본인(기본 배송지), 1 선물
	@Column
	private String name;
	@Column
	private String tel;
	@Column
	private String address;
	@CreationTimestamp
	@Column
	private Timestamp createDate; //자동
	@Column
	private String requestState; // 요청 0, 기간 만료 1 , 성사 2 
	
	
	
	
	
	
		
	
}
