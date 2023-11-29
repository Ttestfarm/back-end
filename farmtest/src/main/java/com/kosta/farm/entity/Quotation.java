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
public class Quotation {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer quotationId;
	// FK
	@Column
	private Integer requestId;
	@Column
	private Integer farmerId;
	
	@Column
	private String quotationProduct; // 품목명
	@Column
	private String quotationQuantity; // 수량
	@Column
	private String quotationPrice; // 가격
	@Column
	private String quotationComment; // 추가 설명
	@Column
	private String quotationPicture; // 농산물 사진 5장
	@Column
	private Date createDate; // 견적서 보낸 날짜
	@Column
	private String state; // 대기중, 견적서 실패, 결제완료

}
