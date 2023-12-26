package com.kosta.farm.dto;

import lombok.Data;

@Data
public class ReviewDto {
	private String receiptId;
	private Integer rating;
	private String content;
	private String userName;

}