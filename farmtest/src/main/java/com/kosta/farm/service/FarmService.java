package com.kosta.farm.service;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Review;
import com.kosta.farm.util.PageInfo;

public interface FarmService {
	//상품 등록
	Integer productEnter(Product product, MultipartFile thmbnail, List<MultipartFile> file) throws Exception;
	
	//카테고리로 검색한 리스트
	List<Farmer> searchListByPage(String category,PageInfo pageInfo) throws Exception;
	//파머 등록
	void regFarmer(Farmer farmer) throws Exception;
	
	List<Farmer> getFarmerByReviewCount(Integer reviewCount) throws Exception;
	
	//farmer별로 리뷰리스트 가져오기
	List<Review> getReviewListByFarmer(Integer farmerId) throws Exception;
	
	List<Review> getReviewListByUser(Integer userId) throws Exception;
	
	void insertReview(Review review) throws Exception;
	
	List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception;
	
	Long farmerCount() throws Exception;
	
	Boolean selectFarmerfollow(Integer userId, Integer farmerId) throws Exception;
	
	Boolean selectedFarmerfollow(Integer userId, Integer farmerId) throws Exception;
	
	Farmer farmerInfo(Integer farmerId) throws Exception;
	
	List<Farmerfollow> getFollowingFarmersByUserId(Integer userId) throws Exception;
	
	void readImage(Integer num, ServletOutputStream outputStream) throws Exception;

	
	
}
