package com.kosta.farm.dto;

import lombok.Data;

@Data
public class QuotationDto {
	private Long quotationId;
	private Long requestId;
	private Long farmerId;
	
	private String quotationProduct;
	private String quotationQuantity;
	private Integer quotationPrice;
	private Integer quotationDelivery;
	private String quotationComment;
	private String quotationImages;
	
	private String address2;
	private String newState;
}
