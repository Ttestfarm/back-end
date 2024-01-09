package com.kosta.farm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
	private Long userId;
	private String receiptId;
	private Integer rating;
	private String content;
	private String userName;
}