package com.kosta.farm.dto;

import com.kosta.farm.entity.Review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderHistoryDto {
	// 주문정보
	private PayInfoSummaryDto payInfoSummaryDto;
	// 리뷰정보
	private Review review;
	// 상품정보

	public Review getReview() {
		return this.review;
	}
}
