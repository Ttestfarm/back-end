package com.kosta.farm.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QFarmerfollow;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.Review;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FarmDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	// 파머 수 가져오기 
	public Long findFarmerCount() throws Exception {
		QFarmer farmer = QFarmer.farmer;
		return jpaQueryFactory.select(farmer.count()).from(farmer).fetchOne();
	}
	
	// 파머 리스트 가져오기
	

	// farmerfollow에서 데이터 조회하기 
	public Farmerfollow findFarmerfollow(Integer id, Integer fid) throws Exception {
		QFarmerfollow farmerfollow = QFarmerfollow.farmerfollow;
		return jpaQueryFactory.select(farmerfollow).from(farmerfollow)
				.where(farmerfollow.userId.eq(id).and(farmerfollow.farmerId.eq(fid))).fetchOne();

	}

	// farmerfollow에서 데이터 조회하기(개수) 좋아요 여부
	public Long findIsFarmerfollow(Integer id, Integer fid) throws Exception {
		QFarmerfollow farmerfollow = QFarmerfollow.farmerfollow;
		return jpaQueryFactory.select(farmerfollow.count()).from(farmerfollow)
				.where(farmerfollow.userId.eq(id).and(farmerfollow.farmerId.eq(fid))).fetchOne();
	}
	
	public List<Farmer> findFarmerfollowListByUser(Integer userId) throws Exception{
		QFarmer farmer= QFarmer.farmer;
		QFarmerfollow farmerfollow =  QFarmerfollow.farmerfollow;
		return jpaQueryFactory.selectFrom(farmer).join(farmerfollow).on(farmer.farmerId.eq(farmerfollow.farmerId)).fetch();
	}

	
	//farmerId로 리뷰 리스트 가져오기
	public List<Review> findByFarmerId(Integer farmerId) throws Exception{
		QReview review= QReview.review;
		return jpaQueryFactory.selectFrom(review).where(review.farmerId.eq(farmerId)).fetch();
	}
	
	//userId로 리뷰 리스트 가져오기
	public List<Review> findByUserId(Integer userId) throws Exception{
		QReview review= QReview.review;
		return jpaQueryFactory.selectFrom(review).where(review.userId.eq(userId)).fetch();
	}
	
	

}