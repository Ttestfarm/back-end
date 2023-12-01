package com.kosta.farm.service;

import java.util.List;

import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.unti.PageInfo;

public interface FarmerService {
	// 파머페이지 : 요청서 보기
	public List<Request> findRequestsByFarmInterest(String farmInterest) throws Exception;
	
	// 견적서 양식 (보내기 이벤트)-> 견적서 저장
	void saveQuotation(Quotation quotation) throws Exception;
	
	// 파머페이지 견적현황 state : 0 (대기중), 1(기간만료), 2(결제완료)  
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageinfo) throws Exception;
	
	// 견적현황 (견적서 취소 이벤트) -> 견적서 삭제
	
	// 견적서 상세보기
}
