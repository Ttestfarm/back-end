//package com.kosta.farm.repository;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Repository;
//
//import com.kosta.farm.entity.QOrders;
//import com.kosta.farm.entity.QQuotation;
//import com.kosta.farm.entity.QRequest;
//import com.kosta.farm.entity.QUser;
//import com.kosta.farm.unti.PageInfo;
//import com.querydsl.core.Tuple;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//
//import lombok.RequiredArgsConstructor;
//
//@Repository
//@RequiredArgsConstructor
//public class FarmerDslRepository {
//	@Autowired
//	private JPAQueryFactory jpaQueryFactory;
//	
//	// 파머페이지 견적현황 state : 0 (대기중), 1(기간만료), 2(결제완료) , 페이지 정보
//	public List<Tuple> findQuotationByFarmerIdAndStateAndPaging(Long farmerId, String state, PageRequest pageRequest) {
//		QQuotation quotation = QQuotation.quotation;
//		QRequest request = QRequest.request;
//		return jpaQueryFactory.select(quotation, request.address).distinct()
//				.from(quotation)
//				.join(request)
//				.on(quotation.requestId.eq(request.requestId))
//				.where(quotation.farmerId.eq(farmerId)
//						.and(quotation.state.eq(state))
//						.and(request.requestState.eq("1"))
//						)
//				.orderBy(quotation.quotationId.desc())
//				.offset(pageRequest.getOffset())
//				.limit(pageRequest.getPageSize())
//				.fetch();
//	}
//	
//	// 
//	public Long findQuotationCountByFarmerId(Long farmerId, String state) {
//		QQuotation quotation = QQuotation.quotation;
//		return jpaQueryFactory.select(quotation.count())
//				.from(quotation)
//				.where(quotation.farmerId.eq(farmerId)
//						.and(quotation.state.eq(state)))
//				.fetchOne();
//	}
//	
//	public List<Tuple> findOrdersByFarmerIdAndOrdersState(Long farmerId, String state, PageInfo pageInfo) {
//		QOrders orders = QOrders.orders;
//		QRequest request = QRequest.request;
//		QUser user = QUser.user;
//		
//		return jpaQueryFactory.select(orders)
//				.from(orders)
//				.join(orders.orders )
//	}
//
//}
