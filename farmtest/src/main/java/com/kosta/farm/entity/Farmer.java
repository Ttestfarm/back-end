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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Farmer {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long farmerId;
	@Column
	private String farmName; // 팜 이름
	@Column
	private String farmPixurl; // 팜 사진
	@Column
	private String farmTel; // 팜 전화번호
	@Column
	private String farmAddress; // 팜 주소1
	@Column
	private String farmAddressDetail; // 팜 주소2
	@Column
	private String registrationNum; // 사업자등록번호
	@Column
	private String farmBank; // 팜 계좌 은행
	@Column
	private String farmAccountNum; // 팜 계좌번호
	@Column(nullable = true)
	private String farmInterest1; // 관심 농산물
	@Column(nullable = true)
	private String farmInterest2;
	@Column(nullable = true)
	private String farmInterest3;
	@Column(nullable = true)
	private String farmInterest4;
	@Column(nullable = true)
	private String farmInterest5;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Builder.Default
	private boolean farmerState = true; // 0 탈퇴 , 1 계정유지
	@Column
	@ColumnDefault("0")
	private Integer followCount;
	@Column
	@ColumnDefault("0")
	private Integer reviewCount;
	@Column
	private double rating;
}
