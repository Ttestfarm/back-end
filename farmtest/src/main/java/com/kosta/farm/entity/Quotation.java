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
public class Quotation {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long quotationId;
	// FK
	@Column
	private Long requestId;
	@Column
	private Long farmerId;
	
	@Column
	private String quotationProduct; // 품목명
	@Column
	private String quotationQuantity; // 수량
	@Column
	private Integer quotationPrice; // 가격
	@Column
	private String quotationComment; // 추가 설명
	@Column
	private String quotationPicture; // 농산물 사진 5장
	@Column
	@CreationTimestamp
	private Timestamp createDate; // 견적서 보낸 날짜
	@Column
//	@Builder.Default
	@ColumnDefault("1")
	private String quotationState = "1"; // 0 : 견적서 취소, 1 : 대기중, 2 : 기간 만료, 3 : 결제완료 
}
