package com.kosta.farm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.QuotationRepositrory;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.unti.PageInfo;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

	// Repository
	private final FarmerRepository farmerRepository;
	private final RequestRepository requestRepository;
	private final QuotationRepositrory quotationRepositrory;
	// DSL
	private final FarmerDslRepository farmerDslRepository;
	private final ObjectMapper objectMapper;
	
	@Override // 요청서 보기
	public List<Request> findRequestsByFarmInterest(String farmInterest) throws Exception {
		return requestRepository.findRequestByRequestProduct(farmInterest);
	}

	@Override // 견적서 양식 (보내기 이벤트)-> 견적서 저장 
	public void saveQuotation(Quotation quotation) throws Exception {
		quotationRepositrory.save(quotation);
	}

	@Override // 파머페이지 견적현황 state : 0 (대기중), 1(기간만료), 2(결제완료)  
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<Tuple> tuples = farmerDslRepository.findQuotationByFarmerIdAndStateAndPaging(farmerId, state, pageRequest);
		System.out.println(farmerId + " " + state + " ");
//		System.out.println(tuples.get(0).get(0, Quotation.class));
		List<QuotationDto> resList = new ArrayList<>();
		
		for(Tuple t : tuples) {
			QuotationDto dto = new QuotationDto();
			Quotation quot = t.get(0, Quotation.class);
			String address = t.get(1, String.class);
			
			dto.setQuotationId(quot.getQuotationId());
			dto.setProduct(quot.getQuotationProduct());
			dto.setQuantity(quot.getQuotationQuantity());
			dto.setPrice(quot.getQuotationPrice());
			dto.setAddress(address);
			dto.setState(quot.getState());
			resList.add(dto);
		}
		System.out.println(resList.get(0).getQuotationId());
		
		Long allCount = farmerDslRepository.findQuotationCountByFarmerId(farmerId, state);
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return resList;
	}
	
	
}
