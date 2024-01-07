package com.kosta.farm.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReviewDto {
	private Long userId;
	private String receiptId;
	private Integer rating;
	private String content;
	private String userName;
}