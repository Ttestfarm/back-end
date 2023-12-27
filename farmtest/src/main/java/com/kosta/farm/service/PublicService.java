package com.kosta.farm.service;

import java.io.OutputStream;
import java.util.List;

import com.kosta.farm.dto.CompanyDto;
import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.RequestStatus;

public interface PublicService {
	public void readImage(Integer num, OutputStream out) throws Exception;
	// 택배 API
	public List<CompanyDto> requestCompanyList() throws Exception;
	
	// 매칭 리스트 가져오기 매칭 메인 페이지
	List<RequestDto> requestListByPage(PageInfo pageInfo) throws Exception;

	Long countFarmersWithRating() throws Exception;
	// 모든 농부들의 별점 평균
	Double avgTotalRating() throws Exception;
	// 모든 requestcount을 state별로
	Long requestCountByState(RequestStatus state) throws Exception;
	// 소트별로
	List<FarmerInfoDto> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception;
	// 키워드로 농부 검색하기
	List<FarmerInfoDto> farmerSearchList(String sortType, String keyword, PageInfo pageInfo) throws Exception;
	
}
