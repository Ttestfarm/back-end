package com.kosta.farm.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.PayInfoSummaryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.QuotationInfoDto;
import com.kosta.farm.dto.QuotePayDto;
import com.kosta.farm.dto.RequestCopyDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.ReviewDto;
import com.kosta.farm.dto.ReviewInfoDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.PaymentStatus;
import com.kosta.farm.util.RequestStatus;
import com.querydsl.core.Tuple;

public interface FarmService {

	// 리뷰 등록
	void addReview(String receiptId, MultipartFile reviewpixUrl, Integer rating, String content) throws Exception;

	// 모든 파머 가져오기
	List<Farmer> findAllFarmers() throws Exception;

	List<Farmer> getFarmerByReviewCount(Integer reviewCount) throws Exception;

	// farmer별로 리뷰리스트 가져오기
	List<Review> getReviewListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception;

	List<ReviewInfoDto> getReviewListInfoByFarmer(Long farmerId, PageInfo pageInfo) throws Exception;

	// farmer별로 product list가져오기
	List<Product> getProductListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception;

	List<Review> getReviewListByUser(Long userId) throws Exception;

	// 구매내역
	List<PayInfo> getOrdersListByUser(Long userId) throws Exception;

	List<PayInfo> getOrdersandReviewByUser(Long userId) throws Exception;

	// 요청서 등록
	Request addRequest(RequestDto request) throws Exception;

	// 파머리스트 페이지
	List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception;

	List<FarmerInfoDto> findfarmerDetail(Long farmerId) throws Exception;
	// 파머리스트 페이지 (무한스크롤)

	Long farmerCount() throws Exception;

	// 파머 상세 페이지
	Farmer farmerDetail(Long farmerId) throws Exception;
	
	// 요청서 따라하기
	RequestCopyDto requestCopy(Long requestId) throws Exception;

	// 파머 팔로우 하기
	Boolean farmerfollow(Long userId, Long farmerId) throws Exception;

	// 이미 파머팔로우완료 한거
	Boolean selectedFarmerfollow(Long userId, Long farmerId) throws Exception;

	Farmer farmerInfo(Long farmerId) throws Exception;

	List<Farmerfollow> getFollowingFarmersByUserId(Long userId, PageInfo pageInfo) throws Exception;

	void readImage(Integer num, ServletOutputStream outputStream) throws Exception;

	List<Request> requestListByUser(Long userId) throws Exception;

	List<Quotation> quoteListByRequest(Long requestId) throws Exception;

	List<Tuple> quoteandRequestListByRequestId(Long requestId) throws Exception;

	// 견적서 수 요청서id별로
	Long quoteCount(Long requestId) throws Exception;

	Map<String, Object> quoteWithFarmerByRequestId(Long requestId) throws Exception;

	ProductInfoDto getProductInfoByProductId(Long productId) throws Exception;

	QuotationInfoDto getQuotationInfoFromOrder(PayInfo payInfo) throws Exception;

	void savePaymentInfo(PayInfo paymentInfo) throws Exception;

	QuotePayDto getQuoteWithRequestInfoById(Long quotationId) throws Exception;

	List<PayInfoSummaryDto> findBuyListByUserAndState(PageInfo pageInfo, Long userId, PaymentStatus state)
			throws Exception;

	List<PayInfoSummaryDto> findBuyListByUser(PageInfo pageInfo, Long userId) throws Exception;
	
	void updateRequestState() throws Exception;
	
	
	
	
}