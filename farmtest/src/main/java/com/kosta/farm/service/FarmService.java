package com.kosta.farm.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.FarmerDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.Tuple;

public interface FarmService {
	// 상품 등록
	Long productEnter(Product product, MultipartFile thmbnail, List<MultipartFile> file) throws Exception;

	// 파머 등록
	void regFarmer(Farmer farmer) throws Exception;

	// 모든 파머 가져오기
	List<Farmer> findAllFarmers() throws Exception;

	List<Farmer> getFarmerByReviewCount(Integer reviewCount) throws Exception;

	// farmer별로 리뷰리스트 가져오기
	List<Review> getReviewListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception;

	// farmer별로 product list가져오기
	List<Product> getProductListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception;

	List<Review> getReviewListByUser(Long userId) throws Exception;

	// 구매내역
	List<Orders> getOrdersListByUser(Long userId) throws Exception;

	List<Map<String, Object>> getOrdersandReviewByUser(Long userId) throws Exception;

	// 리뷰 등록
	void addReview(Long ordersId, List<MultipartFile> files, Integer rating, String content) throws Exception;

	// 요청서 등록
	Long addRequest(Request request) throws Exception;

	// 파머리스트 페이지
	List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception;

	// 소트별로
	List<FarmerDto> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception;

	// 파머리스트 페이지 (무한스크롤)

	Long farmerCount() throws Exception;

	// 파머 상세 페이지
	Farmer farmerDetail(Long farmerId) throws Exception;

	// 파머 팔로우 하기
	Boolean farmerfollow(Long userId, Long farmerId) throws Exception;

	// 이미 파머팔로우완료 한거
	Boolean selectedFarmerfollow(Long userId, Long farmerId) throws Exception;

	Farmer farmerInfo(Long farmerId) throws Exception;

	List<Farmerfollow> getFollowingFarmersByUserId(Long userId) throws Exception;

	void readImage(Integer num, ServletOutputStream outputStream) throws Exception;

	// 키워드로 농부 검색하기
	List<FarmerDto> farmerSearchList(String sortType, String keyword, PageInfo pageInfo) throws Exception;

	// 주문 하기
	void makeOrder(Long productId, Long paymentId) throws Exception;

	// 결제 확인
	Boolean checkPaymentState(Long userId) throws Exception;

	// 매칭 리스트 가져오기? 매칭 메인 페이지
	List<Request> requestListByPage(PageInfo pageInfo) throws Exception;

	List<Request> requestListByUser(Long userId) throws Exception;

	List<Quotation> quoteListByRequest(Long requestId) throws Exception;
	
	List<Tuple> quoteandRequestListByRequestId(Long requestId) throws Exception;
	
	//견적서 수 
	Long quoteCount(Long requestId) throws Exception;

}