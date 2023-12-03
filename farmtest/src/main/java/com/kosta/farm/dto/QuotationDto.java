package com.kosta.farm.dto;

import javax.persistence.Column;

import lombok.Data;

@Data
public class QuotationDto {
	private Long quotationId;
	private String product;
	private String quantity;
	private Integer price;
	private String address;
	private String state;
//	private String Comment;
	
	
}
