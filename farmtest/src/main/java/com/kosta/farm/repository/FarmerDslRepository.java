package com.kosta.farm.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.kosta.farm.dto.PaymentDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QPayment;
import com.kosta.farm.entity.QQuotation;
import com.kosta.farm.entity.QRequest;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.util.PaymentStatus;
import com.kosta.farm.util.QuotationStatus;
import com.kosta.farm.util.RequestStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FarmerDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;
	
	// 파머 관심 농산물 조회
	public Farmer findFarmerByFarmerId(Long farmerId) {
		QFarmer farmer = QFarmer.farmer;
		return jpaQueryFactory.selectFrom(farmer)
				.where(farmer.farmerId.eq(farmerId))
				.fetchOne();
	}
	
	
	// 매칭 주문 요청서 리스트
	public List<Request> findRequestByInterestAndFarmerId(Long farmerId, String farmInterest) {
		QRequest req = QRequest.request;
		QQuotation quot = QQuotation.quotation;
		return jpaQueryFactory.selectFrom(req)
				.leftJoin(quot).on(req.requestId.eq(quot.requestId))
				.where(req.requestProduct.eq(farmInterest)
						.and(req.state.eq(RequestStatus.REQUEST))
						.and(
								quot.farmerId.isNull()
								.or(quot.farmerId.ne(farmerId))
								)
						)
				.fetch();
	}
	
	// 파머페이지 견적서현황 state : CANCEL, READY, EXPIRED, COMPLETED
	public List<Tuple> findQuotationByFarmerIdAndStateAndPaging(Long farmerId, String state, PageRequest pageRequest) {
		QQuotation quot = QQuotation.quotation;
		QRequest req = QRequest.request;
		return jpaQueryFactory.select(quot, req.address2)
				.from(quot)
				.join(req).on(req.requestId.eq(quot.requestId))
				.where(quot.farmerId.eq(farmerId)
						.and(quot.state.eq(QuotationStatus.valueOf(state)))
						.and(req.state.eq(RequestStatus.REQUEST)))
				.orderBy(quot.quotationId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 견적서 수
	public Long findQuotationCountByFarmerId(Long farmerId, String state) {
		QQuotation quot = QQuotation.quotation;
		return jpaQueryFactory.select(quot.count())
				.from(quot)
				.where(quot.farmerId.eq(farmerId)
						.and(quot.state.eq(QuotationStatus.READY)))
				.fetchOne();
	}
	
	
	// 견적서 취소 
	public void updateQuotationStateByfarmerIdAndRequestId(Long farmerId, List<Long> ids) {
		QQuotation quot = QQuotation.quotation;
		
		for(Long id : ids) {
			jpaQueryFactory.update(quot)
				.set(quot.state, QuotationStatus.CANCEL)
				.where(quot.farmerId.eq(farmerId).and(quot.quotationId.eq(id)))
				.execute();
		}
	}
	
	// 견적서 자세히보기
	public Quotation findQuotationByQuotationId(Long farmerId, Long quotationId) {
		QQuotation quot = QQuotation.quotation;
		return jpaQueryFactory.selectFrom(quot)
				.where(quot.farmerId.eq(farmerId)
						.and(quot.quotationId.eq(quotationId)))
				.fetchOne();
	}
	
	// 결제 완료 현황 ( 매칭 주문 )
	public List<Tuple> findOrdersQuotByFarmerIdAndPaging(Long farmerId, PageRequest pageRequest) {
		QPayment pay = QPayment.payment;
		QRequest req = QRequest.request;
		QQuotation quot = QQuotation.quotation;
		
		return jpaQueryFactory.select(pay.receiptId, quot.quotationProduct,
				quot.quotationQuantity, quot.quotationPrice
				, req.name, req.tel, req.address1, req.address2, req.address3)
				.from(pay)
				.innerJoin(quot).on(pay.quotationId.eq(quot.quotationId))
				.innerJoin(req).on(pay.requestId.eq(req.requestId))
				.where(pay.farmerId.eq(farmerId)
						.and(pay.state.eq(PaymentStatus.PAID)))
				.orderBy(pay.receiptId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 결제 완료 현황 (받은 주문)
	public List<Payment> findOrdersByFarmerIdAndPaging(Long farmerId, PageRequest pageRequest) {
		QPayment pay = QPayment.payment;
		return jpaQueryFactory.selectFrom(pay)
				.where(pay.farmerId.eq(farmerId)
						.and(pay.quotationId.isNull())
						.and(pay.state.eq(PaymentStatus.PAID)))
				.orderBy(pay.receiptId.desc())
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.fetch();
	}
	
	// 결제 완료(매칭)현환 수
		public Long findOrdersCountByFarmerIdAndQuotationIsNotNull(Long farmerId) {
			QPayment pay = QPayment.payment;
			return jpaQueryFactory.select(pay.count())
					.from(pay)
					.where(pay.farmerId.eq(farmerId)
							.and(pay.quotationId.isNotNull())
							)
					.fetchOne();
		}
		
	// 결제 완료(주문)현황 수
	public Long findOrdersCountByFarmerIdAndQuotationIsNull(Long farmerId) {
		QPayment pay = QPayment.payment;
		return jpaQueryFactory.select(pay.count())
				.from(pay)
				.where(pay.farmerId.eq(farmerId)
						.and(pay.quotationId.isNotNull()))
				.fetchOne();
	}

	// 결제 완료(메칭) 상세 보기
//	public Tuple findOrderByFarmerIdAndOrderIdIsNotNull(Long farmerId, Long ordersId) {
//		QPayment pay = QPayment.payment;
//		QRequest req = QRequest.request;
//		QQuotation quot = QQuotation.quotation;
//		
//		return jpaQueryFactory.select(ord.ordersId,
//				quot.quotationProduct, quot.quotationQuantity, 
//				req.name, req.tel, req.address, quot.quotationPrice,
//				pay.paymentBank, pay.paymentDelivery, pay.state, pay.paymentPrice
//				, pay.createDate
//				)
//				.from(ord)
//				.innerJoin(quot).on(ord.quotationId.eq(quot.quotationId))
//				.join(req).on(ord.requestId.eq(req.requestId))
//				.join(pay).on(ord.paymentId.eq(pay.paymentId))
//				.where(ord.farmerId.eq(farmerId).and(ord.ordersId.eq(ordersId)))
//				.fetchOne();
//	}
	
	// 결제 완료 (주문) 상세 보기
	public Payment findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(Long farmerId, String receiptId) {
		QPayment pay = QPayment.payment;
		
		return jpaQueryFactory.selectFrom(pay)
				.where(pay.quotationId.isNull()
						.and(pay.farmerId.eq(farmerId))
						.and(pay.receiptId.eq(receiptId)))
				.fetchOne();
	}
	
//	// 발송 완료 (ordersId, delivery update)
//	public void insertDeliveryWithOrdersIdAndTCodeAndTInvoice(Long ordersId, String tCode, String tInvoice) {
//		QDelivery deli = QDelivery.delivery;
//		jpaQueryFactory.insert(deli)
//			.set(deli.ordersId, ordersId)
//			.set(deli.tCode, tCode)
//			.set(deli.tInvoice, tInvoice)
//		    .execute();
//	}
//	
//	// 판매 취소(orders state 변경)
//	public void deleteOrderState(Long ordersId, Long farmerId, String cancelText) {
//		QOrders ord = QOrders.orders;
//		jpaQueryFactory.update(ord)
//			.set(ord.ordersState, "0")
//			.set(ord.cancelText, cancelText)
//			.where(ord.farmerId.eq(farmerId)
//					.and(ord.ordersId.eq(ordersId)))
//			.execute();
//	}
//	
//	// 판매 취소(payment state 변경)
//	public void updatePaymentByOrdersId(Long paymentId) {
//		QPayment pay = QPayment.payment;
//		jpaQueryFactory.update(pay)
//			.set(pay.state, "0")
//			.where(pay.paymentId.eq(paymentId))
//			.execute();
//	}
//	
//	// 배송 현황 리스트
//	public List<Tuple> findOrdersIdAndDeliveryAndProductAndByDeliveryState(Long farmerId, String deliveryState, PageRequest pageRequest) {
//		QOrders ord = QOrders.orders;
//		QDelivery deli = QDelivery.delivery;
//		QQuotation quot = QQuotation.quotation;
//		QRequest req = QRequest.request;
//		QProduct prod = QProduct.product;
//		QPayment pay = QPayment.payment;
//		QDeliveryInfo info = QDeliveryInfo.deliveryInfo;
//		
//		return jpaQueryFactory.select(
//						deli.deliveryId,
//						deli.ordersId,
//						deli.tCode,
//						deli.tName,
//						deli.tInvoice,
//						deli.deliveryState,
//						new CaseBuilder()
//							.when(ord.quotationId.isNotNull()).then(quot.quotationProduct)
//							.otherwise(prod.productName),
//						new CaseBuilder()
//							.when(ord.quotationId.isNotNull()).then(quot.quotationQuantity)
//							.otherwise(prod.productQuantity),
//						pay.paymentPrice,
//						new CaseBuilder()
//						.when(ord.quotationId.isNotNull()).then(req.address)
//						.otherwise(info.infoAddress)
//					)
//				.from(ord)
//				.join(deli).on(ord.ordersId.eq(deli.ordersId))
//				.leftJoin(quot).on(ord.quotationId.eq(quot.quotationId))
//				.leftJoin(prod).on(ord.productId.eq(prod.productId))
//				.leftJoin(req).on(quot.requestId.eq(req.requestId))
//				.join(pay).on(ord.paymentId.eq(pay.paymentId))
//				.leftJoin(info).on(deli.deliveryInfoId.eq(info.deliveryInfoId))
//				.where(ord.farmerId.eq(farmerId)
//						.and(deli.deliveryState.eq(deliveryState)))
//				.orderBy(ord.ordersId.desc())
//				.offset(pageRequest.getOffset())
//				.limit(pageRequest.getPageSize())
//				.fetch();
//	}
//	
//	// 배송 현황 테이블 수
//	public Long findDeliveryCountByFarmerIdAndDeliveryState(Long farmerId, String deliveryState) {
//		QOrders ord = QOrders.orders;
//		QDelivery deli = QDelivery.delivery;
//		return jpaQueryFactory.select(ord.count())
//				.from(ord)
//				.join(deli).on(ord.ordersId.eq(deli.ordersId))
//				.where(ord.farmerId.eq(farmerId)
//						.and(deli.deliveryState.eq(deliveryState)))
//				.fetchOne();
//	}
//	
//	// 정산 테이블 생성
//	public void insertInvoice(Long farmerId, Long ordersId, Date date) {
//		QInvoice in = QInvoice.invoice;
//		QPayment pay = QPayment.payment;
//		QOrders ord = QOrders.orders;
//		
//		Tuple result = jpaQueryFactory
//			    .select(new CaseBuilder()
//						.when(ord.quotationId.isNotNull()).then(3)
//						.otherwise(5),
//						pay.paymentPrice)
//			    .from(ord)
//			    .join(pay).on(ord.paymentId.eq(pay.paymentId))
//			    .where(ord.farmerId.eq(farmerId).and(ord.ordersId.eq(ordersId)))
//			    .fetchOne();
//		
//		jpaQueryFactory.insert(in)
//			.set(in.farmerId, farmerId)
//			.set(in.orderId, ordersId)
//			.set(in.invoiceDate1, date)
//			.set(in.invoiceCommission, result.get(0, Long.class))
//			.set(in.invoicePrice, result.get(1, Long.class))
//			.execute();
//	}
//	
////	정산 내역 리스트
////	public List<Tuple> findOrdersIdAndDeliveryAndProductAndByDeliveryState(Long farmerId, String deliveryState, PageRequest pageRequest) {
////		QDelivery deli = QDelivery.delivery;
////		return jpaQueryFactory.select(deli)
////
////	}
//	
//	// 정산 내역 리스트 수 
//	public Long invoiceCountByState(Long farmerId, String deliveryState) {
//		QOrders ord = QOrders.orders;
//		QDelivery deli = QDelivery.delivery;
//		return jpaQueryFactory.select(ord.count())
//				.from(ord)
//				.join(deli).on(ord.ordersId.eq(deli.ordersId))
//				.where(ord.farmerId.eq(farmerId)
//						.and(deli.deliveryState.eq(deliveryState)))
//				.fetchOne();
//	}
}
