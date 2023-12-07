package com.kosta.farm.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.QCategory;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QFarmerfollow;
import com.kosta.farm.entity.QOrders;
import com.kosta.farm.entity.QProduct;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.QUser;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FarmDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;
	private final EntityManager em;

	// 파머 수 가져오기
	public Long findFarmerCount() throws Exception {
		QFarmer farmer = QFarmer.farmer;
		return jpaQueryFactory.select(farmer.count()).from(farmer).fetchOne();
	}

	// farmerfollow에서 데이터 조회하기
	public Farmerfollow findFarmerfollow(Long userId, Long farmerId) throws Exception {
		QFarmerfollow farmerfollow = QFarmerfollow.farmerfollow;
		return jpaQueryFactory.select(farmerfollow).from(farmerfollow)
				.where(farmerfollow.userId.eq(userId).and(farmerfollow.farmerId.eq(farmerId))).fetchOne();

	}

	// farmerfollow에서 데이터 조회하기(개수) 좋아요 여부
	public Long findIsFarmerfollow(Long userId, Long farmerId) throws Exception {
		QFarmerfollow farmerfollow = QFarmerfollow.farmerfollow;
		return jpaQueryFactory.select(farmerfollow.count()).from(farmerfollow)
				.where(farmerfollow.userId.eq(userId).and(farmerfollow.farmerId.eq(farmerId))).fetchOne();
	}

	public List<Farmer> findFarmerfollowListByUser(Integer userId) throws Exception {
		QFarmer farmer = QFarmer.farmer;
		QFarmerfollow farmerfollow = QFarmerfollow.farmerfollow;
		return jpaQueryFactory.selectFrom(farmer).join(farmerfollow).on(farmer.farmerId.eq(farmerfollow.farmerId))
				.fetch();
	}

	// farmerId로 리뷰 리스트 가져오기
	public List<Review> findByFarmerId(Long farmerId) throws Exception {
		QReview review = QReview.review;
		return jpaQueryFactory.selectFrom(review).where(review.farmerId.eq(farmerId)).fetch();
	}

	// farmerId별로 상품 리스트 가져오기
	public List<Product> findProductByFarmerId(Long farmerId) throws Exception {
		QProduct product = QProduct.product;
		return jpaQueryFactory.selectFrom(product).where(product.farmerId.eq(farmerId)).fetch();
	}

	// userId로 리뷰 리스트 가져오기
	public List<Review> findByUserId(Long userId) throws Exception {
		QReview review = QReview.review;
		return jpaQueryFactory.selectFrom(review).where(review.userId.eq(userId)).fetch();
	}
	
	
	// 이미 존재하는 주문 조회
//	public Orders getExistingOrder(Long userId, Long productId) {
//		QOrders orders = QOrders.orders;
//		QUser user = QUser.user;
//		QProduct product = QProduct.product;
//
//		return jpaQueryFactory.selectFrom(orders).where(orders.userId.eq(userId).and(orders.productId.eq(productId)))
//				.fetchOne();
//	}

	
	// 파머의 카테고리 이름 가져오기? ㄴㄴ farminterest 검색하기
	public List<Tuple> getFarmersByCategory(String categoryName) {
		QFarmer farmer = QFarmer.farmer;
		QCategory category = QCategory.category;
		QProduct product = QProduct.product;
		return jpaQueryFactory.select(farmer, category.categoryName).from(product).join(farmer)
				.on(product.farmerId.eq(farmer.farmerId)).join(category).on(product.categoryId.eq(category.categoryId))
				.where(category.categoryName.eq(categoryName)).fetch();
	}

}
