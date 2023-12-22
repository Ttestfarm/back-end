package com.kosta.farm.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kosta.farm.util.RequestStatus;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	@Column
	private Long userId;
	@Column
	private String requestProduct; // 요청 품목
	@Column
	private String requestQuantity; // 요청 물량
	@Column
	private Date requestDate; // 요청 날짜
	@Column
	private String requestMessage; // 요청메세지
	@Column
	private Boolean choiceState; // 0 본인(기본 배송지), 1 선물
	@Column
	private String name;
	@Column
	private String tel;
	@Column
	private String address1;
	@Column
	private String address2;
	@Column
	private String address3;
	@CreationTimestamp
	@Column
	private Date createDate; // 자동
	@Column
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private RequestStatus state = RequestStatus.REQUEST; // EXPIRED, REQUEST, MATCHED, CANCEL

}
