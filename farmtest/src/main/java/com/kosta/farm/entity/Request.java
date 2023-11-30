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
	private Integer requestId;
	@Column
	private Integer userId;
	@Column
	private String requestProduct; //요청 품목
	@Column
	private String requestQuantity; //요청 물량
	@Column
	private String requestDate; // 요청 날짜
	@Column
	private String requestMessage; //요청메세지
	@Column
	private String address;
	@Column
	private Boolean requestState; //요청 상태 (성사(결제까지완료) or not)
	@CreationTimestamp
	@Column
	private Timestamp createDate; //자동
	
	
	
	
	
	
		
	
}
