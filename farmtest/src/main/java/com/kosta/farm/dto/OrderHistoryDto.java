package com.kosta.farm.dto;

import java.sql.Timestamp;

import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderHistoryDto {
	// 주문정보
//	private Long ordersId; //주문번호
//	private Timestamp date; //날짜
//	private Long productId; //프로덕트 아이디
//	private Long deliveryId; // 배송
//	private Long farmerId; // 농부id
//	private Long quotationId; //요청서 아이디?
//	private Long userId; //주문회원
//	private String ordersState;
//	private String status; //배송 완료, 배송 중 , 결제 취소 결제 완료 전체
	private Orders orders;
	// 리뷰정보
	private Review review;

	public Review getReview() {
		return this.review;
	}
}
