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
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Farmer {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer farmerId;
	@Column
	private String farmName;
	@Column
	private String userEmail;
	@Column
	private String farmPixurl;
	@Column
	private String farmTel;
	@Column
	private String farmAddress;
	@Column
	private String registrationNum;
	@Column
	private String farmAccountNum;
	@Column
	private String interest1;
	@Column
	private String interest2;
	@Column
	private String interest3;
	@Column
	private String interest4;
	@Column
	private String interest5;
	@Column
	private boolean state;
	@CreationTimestamp
	@Column
	private Timestamp createDate;
}