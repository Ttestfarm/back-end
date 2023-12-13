package com.kosta.farm.dto;

import lombok.Data;

@Data
public class ReviewDto {
	private Long ordersId;
	private Integer rating;
	private String content;
}