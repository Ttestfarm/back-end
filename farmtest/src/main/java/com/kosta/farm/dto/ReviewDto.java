package com.kosta.farm.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReviewDto {
	private Long ordersId;
	private Integer rating;
	private String content;
	private String reviewpixUrl;
}
