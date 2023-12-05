package com.kosta.farm.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.kosta.farm.entity.QOrders;
import com.kosta.farm.entity.QPayment;
import com.kosta.farm.entity.QProduct;
import com.kosta.farm.entity.QQuotation;
import com.kosta.farm.entity.QRequest;
import com.kosta.farm.entity.QUser;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FarmerDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;
	
	public List<Request> findRequestByInterestAndFarmerId(Long farmerId, String farmInterest) {
		QRequest req = QRequest.request;
		QQuotation quot = QQuotation.quotation;
		return jpaQueryFactory.selectFrom(req)
				.leftJoin(quot)
				.on(req.requestId.eq(quot.requestId))
				.where(req.requestProduct.eq(farmInterest)
						.and(quot.quotationId.isNull()))
				.fetch();
	}
	
	// 파머페이지 견적현황 state : 0 (대기중), 1(기간만료), 2(결제완료) , 페이지 정보
	public List<Tuple> findQuotationByFarmerIdAndStateAndPaging(Long farmerId, String state, PageRequest pageRequest) {
		QQuotation quotation = QQuotation.quotation;
		QRequest request = QRequest.request;
		return jpaQueryFactory.select(quotation, request.address).distinct()
				.from(quotation)
				.join(request)
				.on(quotation.requestId.eq(request.requestId))
				.where(quotation.farmerId.eq(farmerId)
						.and(quotation.quotationState.eq(state))
						.and(request.requestState.eq("1"))
						)
				.orderBy(quotation.quotationId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 
	public Long findQuotationCountByFarmerId(Long farmerId, String state) {
		QQuotation quotation = QQuotation.quotation;
		return jpaQueryFactory.select(quotation.count())
				.from(quotation)
				.where(quotation.farmerId.eq(farmerId)
						.and(quotation.quotationState.eq(state)))
				.fetchOne();
	}
	
	
	// 견적서 취소 
	public void updateQuotationStateByfarmerIdAndRequestId(Long farmerId, List<Long> ids) {
		QQuotation quot = QQuotation.quotation;
		for(Long id : ids) {
			jpaQueryFactory.update(quot)
				.set(quot.quotationState, "0")
				.where(quot.farmerId.eq(farmerId).and(quot.requestId.eq(id)))
				.execute();
		}
	}
	
	// 견적서 자세히보기
	public Quotation findQuotationByQuotationId(Long quotationId) {
		QQuotation quot = QQuotation.quotation;
		return jpaQueryFactory.selectFrom(quot)
				.where(quot.quotationId.eq(quotationId))
				.fetchOne();
	}
	
	// 결제 완료 현환 ( 매칭 주문 )
	public List<Tuple> findOrdersQuotByFarmerIdAndPaging(Long farmerId, PageRequest pageRequest) {
		QOrders ord = QOrders.orders;
		QRequest req = QRequest.request;
		QQuotation quot = QQuotation.quotation;
		
		return jpaQueryFactory.select(ord.ordersId, quot.quotationProduct,
				quot.quotationQuantity, quot.quotationPrice
				, req.name, req.tel, req.address)
				.from(ord)
				.join(quot)
				.on(ord.quotationId.eq(quot.quotationId))
				.join(req)
				.on(ord.requestId.eq(req.requestId))
				.where(ord.quotationId.isNotNull()
						.and(ord.farmerId.eq(farmerId))
						.and(ord.ordersState.eq("0")))
				.orderBy(ord.ordersId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 결제 완료 현황 (받은 주문)
	public List<Tuple> findOrdersByFarmerIdAndPaging(Long farmerId, PageRequest pageRequest) {
		QOrders ord = QOrders.orders;
		QRequest req = QRequest.request;
		QProduct prod = QProduct.product;
		return jpaQueryFactory.select(ord.ordersId, prod.productName, prod.productQuantity
				, prod.productPrice, req.name, req.tel, req.address)
				.from(ord)
				.join(prod)
				.on(ord.productId.eq(prod.productId))
				.join(req)
				.on(ord.requestId.eq(req.requestId))
				.where(ord.quotationId.isNull()
						.and(ord.farmerId.eq(farmerId))
						.and(ord.ordersState.eq("0")))
				.orderBy(ord.ordersId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 결제 완료(매칭)현환 총 수
		public Long findOrdersCountByFarmerIdAndQuotationIsNotNull(Long farmerId) {
			QOrders ord = QOrders.orders;
			return jpaQueryFactory.select(ord.count())
					.from(ord)
					.where(ord.farmerId.eq(farmerId)
							.and(ord.quotationId.isNotNull()))
					.fetchOne();
		}
		
	// 결제 완료(주문)현환 총 수
	public Long findOrdersCountByFarmerIdAndQuotationIsNull(Long farmerId) {
		QOrders ord = QOrders.orders;
		return jpaQueryFactory.select(ord.count())
				.from(ord)
				.where(ord.farmerId.eq(farmerId)
						.and(ord.quotationId.isNotNull()))
				.fetchOne();
	}

	// 결제 완료(메칭) 상세 보기
	public Tuple findOrderByFarmerIdAndOrderIdIsNotNull(Long farmerId, Long ordersId) {
		QOrders ord = QOrders.orders;
		QRequest req = QRequest.request;
		QQuotation quot = QQuotation.quotation;
		QPayment pay = QPayment.payment;
		
		return jpaQueryFactory.select(ord.ordersId, pay.createDate,
				quot.quotationProduct, quot.quotationQuantity, 
				req.name, req.tel, req.address, pay.paymentBank,
				quot.quotationPrice,pay.paymentDelivery)
				.from(ord)
				.join(quot).on(ord.quotationId.eq(quot.quotationId))
				.join(req).on(ord.requestId.eq(req.requestId))
				.join(pay).on(ord.paymentId.eq(pay.paymentId))
				.where(ord.farmerId.eq(farmerId).and(ord.ordersId.eq(ordersId))
						.and(ord.quotationId.isNotNull()))
				.fetchOne();
	}
	
	// 결제 완료 (주문) 상세 보기
	public Tuple findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(Long farmerId, Long ordersId) {
		QOrders ord = QOrders.orders;
		QRequest req = QRequest.request;
		QProduct prod = QProduct.product;
		QPayment pay = QPayment.payment;
		
		return jpaQueryFactory.select(ord.ordersId, pay.createDate, prod.productName,
				prod.productQuantity, req.name, req.tel, req.address,
				pay.paymentBank, prod.productPrice, pay.paymentDelivery)
				.from(ord)
				.join(prod).on(ord.productId.eq(prod.productId))
				.join(req).on(ord.requestId.eq(req.requestId))
				.join(pay).on(ord.paymentId.eq(pay.paymentId))
				.where(ord.quotationId.isNull()
						.and(ord.farmerId.eq(farmerId))
						.and(ord.ordersId.eq(ordersId)))
				.fetchOne();
	}
}
