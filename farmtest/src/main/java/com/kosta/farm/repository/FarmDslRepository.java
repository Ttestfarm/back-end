package com.kosta.farm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.OrderHistoryDto;
import com.kosta.farm.dto.PayInfoSummaryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.ReviewInfoDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QFarmerfollow;
import com.kosta.farm.entity.QPayInfo;
import com.kosta.farm.entity.QProduct;
import com.kosta.farm.entity.QQuotation;
import com.kosta.farm.entity.QRequest;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.QUser;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.RequestStatus;
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

	public List<Tuple> getRequestsWithUsername(PageInfo pageInfo) {
		QRequest request = QRequest.request;
		QUser user = QUser.user;
		List<Tuple> result = jpaQueryFactory.select(request, user.userName).from(request).leftJoin(user)
				.on(request.userId.eq(user.userId)).fetch();
		return result;
	}

	public Long requestAllCount() throws Exception {
		QRequest request = QRequest.request;
		return jpaQueryFactory.select(request.count()).from(request).fetchOne();
	}

	public List<RequestDto> requestListWithNameByPage(PageInfo pageInfo) throws Exception {
		QRequest request = QRequest.request;
		QUser user = QUser.user;
		Long count = requestAllCount();
		pageInfo.setAllPage((int) Math.ceil(count.intValue() / 9));
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 9);

		System.out.println(pageRequest.getOffset());
		System.out.println(pageRequest.getPageSize());
		List<Tuple> tupleList = jpaQueryFactory.select(request, user.userName).from(request).leftJoin(user)
				.on(request.userId.eq(user.userId)).offset(pageRequest.getOffset()).limit(pageRequest.getPageSize())
				.where(request.state.eq(RequestStatus.REQUEST).or(request.state.eq(RequestStatus.MATCHED)))
				.orderBy(request.requestId.desc()).fetch(); // requestStatus가 request상태거나 matched상태 개수만 보여준다

		List<RequestDto> list = new ArrayList<>();
		for (Tuple t : tupleList) {
			Request req = t.get(0, Request.class);
			RequestDto reqDto = objectMapper.convertValue(req, RequestDto.class);
			reqDto.setUserName(t.get(1, String.class));
			list.add(reqDto);
		}
		return list;
	}

	public Long reviewCountByFarmer(Long farmerId) throws Exception {
		QReview review = QReview.review;
		return jpaQueryFactory.select(review.count()).from(review).where(review.farmerId.eq(farmerId)).fetchOne();
	}

//리뷰인포 가져오기
	public List<ReviewInfoDto> reviewListWithFarmNameByPage(Long farmerId, PageRequest pageRequest) throws Exception {
		QReview review = QReview.review;
		QFarmer farmer = QFarmer.farmer;
		QPayInfo payInfo = QPayInfo.payInfo;

		List<Tuple> tupleList = jpaQueryFactory.select(review, farmer.farmName, payInfo.count, payInfo.productName)
				.from(review)
				.leftJoin(farmer)
				.on(review.farmerId.eq(farmer.farmerId).and(farmer.farmerId.eq(farmerId)))
				.leftJoin(payInfo)
				.on(review.receiptId.eq(payInfo.receiptId))
		        .where(review.farmerId.eq(farmerId)) //review의 farmerId랑 매칭
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize()).orderBy(review.reviewId.desc()).fetch();

		List<ReviewInfoDto> list = new ArrayList<>();
		for (Tuple t : tupleList) {
			Review rev = t.get(0, Review.class); // tuple에서 review 객체 가져오기
			ReviewInfoDto revDto = objectMapper.convertValue(rev, ReviewInfoDto.class); // Review 객체를 ReviewInfoDto로 변환
			revDto.setFarmName(t.get(1, String.class));// tuple에서 farname가져오기
			revDto.setCount(t.get(2, Integer.class));
			revDto.setProductName(t.get(3, String.class)); // tuple에서 payInfo정보 가져오기
			list.add(revDto);
		}
		return list;
	}

	public Long payInfoAllCount(Long userId) throws Exception {
		QPayInfo payInfo = QPayInfo.payInfo;
		return jpaQueryFactory.select(payInfo.count()).from(payInfo).where(payInfo.userId.eq(userId)).fetchOne();
	}

	public List<PayInfoSummaryDto> getPartialOrdersListByUserByPage(PageInfo pageInfo, Long userId) throws Exception {
		QPayInfo payInfo = QPayInfo.payInfo;
		QProduct product = QProduct.product;
		QFarmer farmer = QFarmer.farmer;
		QQuotation quotation = QQuotation.quotation;

		Long cnt = payInfoAllCount(userId);
		System.out.println("hwerererere" + cnt);

		pageInfo.setAllPage((int) Math.ceil(cnt.intValue() / 6));
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 6,
				Sort.by(Sort.Direction.DESC, "createAt"));
		List<Tuple> tupleList = jpaQueryFactory
				.select(payInfo.receiptId, payInfo.ordersId, payInfo.farmerId, payInfo.quotationId, payInfo.productId,
						payInfo.userId, payInfo.paymentMethod, payInfo.paymentDelivery, payInfo.productPrice,
						payInfo.count, payInfo.amount, payInfo.productName, payInfo.buyerAddress, payInfo.buyerName,
						payInfo.buyerTel, payInfo.createAt, payInfo.state, product.thumbNail, farmer.farmName,
						quotation.quotationImages)
				.from(payInfo).leftJoin(product).on(payInfo.productId.eq(product.productId)).leftJoin(farmer)
				.on(payInfo.farmerId.eq(farmer.farmerId)).leftJoin(quotation)
				.on(payInfo.quotationId.eq(quotation.quotationId)).where(payInfo.userId.eq(userId))
				.offset(pageRequest.getOffset()).limit(pageRequest.getPageSize()).orderBy(payInfo.createAt.desc())
				.fetch();

		List<PayInfoSummaryDto> list = new ArrayList<>();
		for (Tuple t : tupleList) {
			PayInfoSummaryDto dto = new PayInfoSummaryDto();
			dto.setCreateAt(t.get(payInfo.createAt));
			dto.setReceiptId(t.get(payInfo.receiptId));
			dto.setOrdersId(t.get(payInfo.ordersId));
			dto.setUserId(t.get(payInfo.userId));
			dto.setFarmerId(t.get(payInfo.farmerId));
			dto.setPaymentMethod(t.get(payInfo.paymentMethod));
			dto.setPaymentDelivery(t.get(payInfo.paymentDelivery));
			dto.setProductId(t.get(payInfo.productId));
			dto.setProductPrice(t.get(payInfo.productPrice));
			dto.setCount(t.get(payInfo.count));
			dto.setAmount(t.get(payInfo.amount));
			dto.setProductName(t.get(payInfo.productName));
			dto.setBuyerAddress(t.get(payInfo.buyerAddress));
			dto.setBuyerName(t.get(payInfo.buyerName));
			dto.setBuyerTel(t.get(payInfo.buyerTel));
			dto.setState(t.get(payInfo.state));
			dto.setThumbNail(t.get(product.thumbNail));
			dto.setFarmName(t.get(farmer.farmName));
			dto.setQuotationImages(t.get(quotation.quotationImages));

			list.add(dto);
		}
		return list;
	}

	public Map<String, Object> findQuotationsWithFarmerByRequestId(Long requestId) {
		QQuotation quotation = QQuotation.quotation;
		QFarmer farmer = QFarmer.farmer;

		List<Tuple> quotesWithFarmer = jpaQueryFactory
				.select(quotation, farmer.farmName, farmer.farmAddress, farmer.farmPixurl, farmer.rating,
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

	public List<PayInfo> findPayInfowithReviewByUserId(Long userId) {
		QPayInfo payInfo = QPayInfo.payInfo;
		QReview review = QReview.review;
		return jpaQueryFactory.select(payInfo).from(payInfo).leftJoin(review).on(payInfo.receiptId.eq(review.receiptId))
				.where(payInfo.userId.eq(userId)).fetch();

	}

}