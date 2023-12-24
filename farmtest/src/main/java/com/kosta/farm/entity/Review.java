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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;
	@ColumnDefault("0")
	@Column
	private Integer rating; // 별점
	@Column
	private String content;
	@Column
	private String reviewpixUrl;
	@Column
	private Boolean reviewState;
	@CreationTimestamp
	@Column
	private Timestamp createDate;
	@Column
	private Long farmerId;
	@Column
	private Long userId;
	@Column
	private String userName;
	@Column
	private String receiptId;

}
