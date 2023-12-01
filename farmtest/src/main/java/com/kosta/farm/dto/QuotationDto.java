package com.kosta.farm.dto;

import javax.persistence.Column;

import lombok.Data;

@Data
public class QuotationDto {
	private Long quotationId;
	@Column
	private String product;
	private String quantity;
	private String price;
	@Column
	private String address;
	private String state;
//	private String Comment;
	
	
}
