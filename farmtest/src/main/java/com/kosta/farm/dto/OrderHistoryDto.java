package com.kosta.farm.dto;

import java.sql.Timestamp;
import java.util.List;

import com.kosta.farm.entity.Orders;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderHistoryDto {
	//주문정보
	private Long ordersId; //주문번호
	private Timestamp date; //날짜
	private Long productId; //프로덕트 아이디
	private Long deliveryId; // 배송
	private Long farmerId; // 농부id
	private Long quotationId; //요청서 아이디?
	private Long userId; //주문회원
	private String ordersState;
	private String status; //배송 완료, 배송 중 , 결제 취소 결제 완료 전체
	//리뷰정보
	private List<ReviewDto> reviewDtoList;
	

	
//	public Orders toEntity() {
//		return Orders
//				.builder()
//				.ordersId(ordersId)
//				.createDate(date)
//				.productId(productId)
//				.farmerId(farmerId)
//				.quotationId(quotationId)
//				.userId(userId)
//				.ordersState(ordersState)
//				
//				;
		
	}
//	public OrderHistoryDto fromOrdersEntityWithReviews(Orders orders) {
//	OrderHistoryDto orderHistoryDto= new OrderHistoryDto();
//	orderHistoryDto.setOrdersId(orders.getOrdersId());
//	orderHistoryDto.setDate(orders.getCreateDate());
//	orderHistoryDto.setProductId(orders.getProductId());
//	orderHistoryDto.setFarmerId(orders.getFarmerId());
//	orderHistoryDto.setQuotationId(orders.getQuotationId());
//	orderHistoryDto.setUserId(orders.getUserId());
//	orderHistoryDto.setOrdersState(orders.getOrdersState());
//	//order필드를 orderhistorydto에설정
//	return orderHistoryDto;
//}
	

