package com.kosta.farm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.QCategory;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QFarmerfollow;
import com.kosta.farm.entity.QOrders;
import com.kosta.farm.entity.QProduct;
import com.kosta.farm.entity.QQuotation;
import com.kosta.farm.entity.QRequest;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.QUser;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FarmDslRepository {
   @Autowired
   private JPAQueryFactory jpaQueryFactory;
   
   @Autowired
   private ObjectMapper objectMapper;

   // quotecount 가져오기
   public Long findQuoteCountByRequestId(Long requestId) throws Exception {
      QQuotation quotation = QQuotation.quotation;
      return jpaQueryFactory.select(quotation.count()).from(quotation).where(quotation.requestId.eq(requestId))
            .fetchOne();
//      return count;
   };

   // 파머 수 가져오기
   public Long findFarmerCount() throws Exception {
      QFarmer farmer = QFarmer.farmer;
      return jpaQueryFactory.select(farmer.count()).from(farmer).fetchOne();
   }

   //

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
   public List<Tuple> getRequestsWithUsername(PageInfo pageInfo){
        QRequest request = QRequest.request;
        QUser user = QUser.user;
      List<Tuple> result=jpaQueryFactory.select(request, user.userName)
            .from(request).leftJoin(user).on(request.userId.eq(user.userId)).fetch();
      return result;
   }
   
   public Long requestAllCount() throws Exception {
        QRequest request = QRequest.request;
      return jpaQueryFactory.select(request.count()).from(request).fetchOne();
   }
   
//   return jpaQueryFactory.select(farmer.count()).from(farmer).fetchOne();
   
   public List<RequestDto> requestListWithNameByPage(PageInfo pageInfo) throws Exception {
        QRequest request = QRequest.request;
        QUser user = QUser.user;
        Long count=requestAllCount();
        pageInfo.setAllPage((int)Math.ceil(count.intValue()/9));
        PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 9);
        
        System.out.println(pageRequest.getOffset());
        System.out.println(pageRequest.getPageSize());
        List<Tuple> tupleList = 
              jpaQueryFactory.select(request,user.userName)
         .from(request)
         .leftJoin(user)
         .on(request.userId.eq(user.userId))
         .offset(pageRequest.getOffset())
         .limit(pageRequest.getPageSize())
         .orderBy(request.requestId.desc()).fetch();
      
        List<RequestDto> list = new ArrayList<>();
        for(Tuple t : tupleList) {
           Request req =  t.get(0, Request.class);
           RequestDto reqDto = objectMapper.convertValue(req,RequestDto.class);
           reqDto.setUserName(t.get(1, String.class));
           list.add(reqDto);
        }
      return list;
   }
   public Map<String, Object> findQuotationsWithFarmerByRequestId(Long requestId) {
      QQuotation quotation = QQuotation.quotation;
      QFarmer farmer = QFarmer.farmer;

      List<Tuple> quotesWithFarmer = jpaQueryFactory
            .select(quotation, farmer.farmName, 
                  farmer.farmAddress, farmer.farmPixurl, farmer.rating,
                  farmer.reviewCount, farmer.followCount)
            .from(quotation).leftJoin(farmer).on(quotation.farmerId.eq(farmer.farmerId))
            .where(quotation.requestId.eq(requestId)).fetch();
      System.out.println(quotesWithFarmer);
      Map<String, Object> res = new HashMap<>();
      List<Map<String, Object>> quotesWithFarmerList = new ArrayList<>();

      for (Tuple tuple : quotesWithFarmer) {
         Quotation quote = tuple.get(quotation);
         String farmName = tuple.get(farmer.farmName);
         String farmAddress = tuple.get(farmer.farmAddress);
         String farmPix = tuple.get(farmer.farmPixurl);
         Double rating = tuple.get(farmer.rating);
         Integer reviewCount = tuple.get(farmer.reviewCount);
         Integer followCount = tuple.get(farmer.followCount);

         Map<String, Object> quoteFarmerMap = new HashMap<>();
         quoteFarmerMap.put("quote", quote);
         quoteFarmerMap.put("farmName", farmName);
         quoteFarmerMap.put("farmAddress", farmAddress);
         quoteFarmerMap.put("farmPix", farmPix);
         quoteFarmerMap.put("rating", rating);
         quoteFarmerMap.put("reviewCount", reviewCount);
         quoteFarmerMap.put("followCount", followCount);

         quotesWithFarmerList.add(quoteFarmerMap);
      }

      res.put("quotesWithFarmer", quotesWithFarmerList);
      return res;
   }

   public List<Tuple> getReqandQuoteByRequestId(Long requestId) {
      QRequest request = QRequest.request;
      QQuotation quotation = QQuotation.quotation;
      return jpaQueryFactory.select(request, quotation).from(request).leftJoin(quotation)
            .on(request.requestId.eq(quotation.requestId)).where(request.requestId.eq(requestId)).fetch();

   }

   public List<Orders> findOrderswithReviewByUserId(Long userId) {
      QOrders orders = QOrders.orders;
      QReview review = QReview.review;
      return jpaQueryFactory.select(orders).from(orders).leftJoin(review).on(orders.ordersId.eq(review.ordersId))
            .where(orders.userId.eq(userId)).fetch();
   }


   @Transactional
   public void updateStock(Long productId, Integer stock) {

   }

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