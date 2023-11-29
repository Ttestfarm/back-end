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
public class Farmer {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer farmerId;
	@Column
	private String farmName; // 팜 이름
	@Column
	private String farmPixurl; // 팜 사진
	@Column
	private String farmTel; // 팜 전화번호
	@Column
	private String farmAddress; // 팜 주소
	@Column
	private String registrationNum; // 사업자등록번호
	@Column
	private String farmAccount; // 팜 계좌번호
	@Column
	private String farmInterest1;
	@Column
	private String farmInterest2;
	@Column
	private String farmInterest3;
	@Column
	private String farmInterest4;
	@Column
	private String farmInterest5;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String state;
}
