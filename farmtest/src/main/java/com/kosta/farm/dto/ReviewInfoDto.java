package com.kosta.farm.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewInfoDto {
	private Long reviewId;
	private Integer rating; // 별점
	private String content;
	private String reviewpixUrl;
	private Boolean reviewState;
	private Timestamp createDate;
	private Long farmerId;
	private Long userId;
	private String userName;
	private String receiptId;
	
	private String farmName;
	private String productName;
	private Integer count; //수량
	

}
