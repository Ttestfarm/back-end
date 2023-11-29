package com.kosta.farm.entity;

import java.sql.Timestamp;

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
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Farmer {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer farmerId;
	
	private String farmName;
	private String userEmail;
	private String farmPixurl;
	private String farmTel;
	private String farmAddress;
	private String registrationNum;
	private String accountNo;
	private String interest1;
	private String interest2;
	private String interest3;
	private String interest4;
	private String interest5;
	private boolean state;
	@CreationTimestamp
	private Timestamp createDate;
}