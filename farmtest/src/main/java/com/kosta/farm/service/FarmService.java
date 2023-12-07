package com.kosta.farm.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Product;
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
	
	//리뷰 등록
	void insertReview(Review review) throws Exception;

	// 파머리스트 페이지
	List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception;

//소트별로
	List<Farmer> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception;

	// 파머리스트 페이지 (무한스크롤)

	Long farmerCount() throws Exception;

	// 파머 상세 페이지
	Farmer farmerDetail(Long farmerId) throws Exception;

	// 파머 팔로우 하기
	Boolean Farmerfollow(Long userId, Long farmerId) throws Exception;

	// 이미 파머팔로우완료 한거
	Boolean selectedFarmerfollow(Long userId, Long farmerId) throws Exception;

	Farmer farmerInfo(Long farmerId) throws Exception;

	List<Farmerfollow> getFollowingFarmersByUserId(Long userId) throws Exception;

	void readImage(Integer num, ServletOutputStream outputStream) throws Exception;

	// 키워드로 농부 검색하기
	List<Farmer> FarmerSearchList(String keyword, PageInfo pageInfo) throws Exception;

	// 주문 구현
	void makeOrder(Orders orders) throws Exception;

	


}
