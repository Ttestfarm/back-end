package com.kosta.farm.service;

import java.util.List;

import com.kosta.farm.dto.DeliveryDto;
import com.kosta.farm.dto.OrdersDto;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.dto.RegFarmerDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.unti.PageInfo;

public interface FarmerService {
	// 매칭 주문 요청서 보기
	// 파머 관심 농산물 리스트
	public List<String> findFarmInterestByFarmerId(Long farmerId) throws Exception;
	// 관심 농산물인 요청서 리스트 보기
	public List<Request> findRequestsByFarmInterest(Long farmerId, String farmInterest) throws Exception;
	
	// 견적서 양식 (보내기 이벤트)-> 견적서 저장
	void saveQuotation(Quotation quotation) throws Exception;
	
	// 파머페이지 견적현황
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageInfo) throws Exception;
	
	// 견적현황 (견적서 취소 이벤트) -> 견적서 삭제
	public void updateQuotationByFarmerIdAndRequestIds(Long farmerId, List<Long> ids) throws Exception;
	
	// 견적서 상세보기
	public Quotation findQuotationByQuotationId(Long quotationId) throws Exception;
	
	// 결제 완료 현황
	public List<OrdersDto> findOrdersByFarmerIdAndPage(Long farmerId, String type, PageInfo pageInfo) throws Exception;

	// 결제 완료(매칭) 상세보기
	public OrdersDto OrdersDetailQuotationId(Long farmerId, Long ordersId) throws Exception;
	// 결제 완료(주문) 상세보기
	public OrdersDto OrdersDetailNotQuotationId(Long farmerId, Long ordersId) throws Exception;
	// 발송 완료 처리
	public void updateDelivery(Long ordersId, String tCode, String tInvoice) throws Exception;
	// 판매 취소 처리
	public void updateOrderState(Long farmerId, Long orderId) throws Exception;
	
	// 배송 현황 리스트
	public List<DeliveryDto> findDeliberyByFarmerIdAndDeliveryState(Long farmerId, String deliveryState, PageInfo pageInfo) throws Exception;
	
	// 파머등록
	Farmer registerFarmer(RegFarmerDto regFarmerDto) throws Exception;
}
