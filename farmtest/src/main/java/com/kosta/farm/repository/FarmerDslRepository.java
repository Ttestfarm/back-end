package com.kosta.farm.repository;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

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
                                                            .or(quot.farmerId.ne(farmerId))))
                        .fetch();
      }

      // 파머페이지 견적서현황 state : CANCEL, READY, EXPIRED, COMPLETED
      public List<Tuple> findQuotationByFarmerIdAndStateAndPaging(Long farmerId, String state,
                  PageRequest pageRequest) {
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

            for (Long id : ids) {
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
                        quot.quotationQuantity, quot.quotationPrice, req.name, req.tel, req.address1, req.address2,
                        req.address3)
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
                                    .and(pay.quotationId.isNotNull()))
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
      public Tuple findOrderByFarmerIdAndOrderIdIsNotNull(Long farmerId, String receiptId) {
            QPayment pay = QPayment.payment;
            QRequest req = QRequest.request;
            QQuotation quot = QQuotation.quotation;
            return jpaQueryFactory.select(pay, quot.quotationProduct, quot.quotationQuantity,
                        quot.quotationPrice, req.name, req.tel, req.address1, req.address2, req.address3)
                        .from(pay)
                        .innerJoin(quot).on(pay.quotationId.eq(quot.quotationId))
                        .join(req).on(pay.requestId.eq(req.requestId))
                        .where(pay.farmerId.eq(farmerId).and(pay.receiptId.eq(receiptId)))
                        .fetchOne();
      }

      // 결제 완료 (주문) 상세 보기
      public Payment findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(Long farmerId, String receiptId) {
            QPayment pay = QPayment.payment;
            return jpaQueryFactory.selectFrom(pay)
                        .where(pay.quotationId.isNull()
                                    .and(pay.farmerId.eq(farmerId))
                                    .and(pay.receiptId.eq(receiptId)))
                        .fetchOne();
      }

      // 발송 완료 처리 *테이블 변경으로 미사용
      // public void updatePaymentDelivery(String receiptId, String tCode, String
      // tName, String tInvoice) {
      // QPayment pay = QPayment.payment;
      // jpaQueryFactory.update(pay)
      // .set(pay.tCode, tCode)
      // .set(pay.tName, tName)
      // .set(pay.tInvoice, tInvoice)
      // .where(pay.receiptId.eq(receiptId))
      // .execute();
      // }

      // 정산 데이터 추가 *테이블 변경으로 미사용
      // public void UpdatePaymentInvoice(String receiptId, Date date, String invoice)
      // {
      // QPayment pay = QPayment.payment;
      // jpaQueryFactory.update(pay)
      // .set(pay.invoiceDate, date)
      // .set(pay.invoiceCommission, )
      // .set(pay.invoicePrice, invoice)
      // .where(pay.invoicePrice.eq(receiptId))
      // .execute();
      // }

      // 판매 취소
      public void updatePaymentStateCANCEL(Long farmerId, String receiptId, String cancelText) {
            QPayment pay = QPayment.payment;
            jpaQueryFactory.update(pay)
                        .set(pay.cancelText, cancelText)
                        .set(pay.state, PaymentStatus.CANCEL)

                        .where(pay.farmerId.eq(farmerId)
                                    .and(pay.receiptId.eq(receiptId)))
                        .execute();
      }

      // 배송 현황 리스트
      public List<Payment> findOrdersIdAndDeliveryAndProductAndByDeliveryState(Long farmerId, String state,
                  PageRequest pageRequest) {
            QPayment pay = QPayment.payment;
            QQuotation quot = QQuotation.quotation;

            // Payment state의 값을 String 클래로 받아서 enum 형태로 변환 후 비교
            PaymentStatus stateValue = null;
            if (state == "SHIPPING") {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송중
            else {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송완료

            return jpaQueryFactory.select(pay)
                        .from(pay)
                        .where(pay.farmerId.eq(farmerId)
                                    .and(pay.state.eq(stateValue)))
                        .orderBy(pay.receiptId.desc())
                        .offset(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .fetch();
      }

      // 배송 현황 테이블 수
      public Long findDeliveryCountByFarmerIdAndDeliveryState(Long farmerId, String state) {
            QPayment pay = QPayment.payment;

            // Payment state의 값을 String 클래로 받아서 enum 형태로 변환 후 비교
            PaymentStatus stateValue = null;
            if (state == "SHIPPING") {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송중
            else {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송완료

            return jpaQueryFactory.select(pay.count())
                        .from(pay)
                        .where(pay.farmerId.eq(farmerId)
                                    .and(pay.state.eq(stateValue)))
                        .fetchOne();
      }

      // 정산 테이블 생성 *테이블 변경으로 미사용
      // public void insertInvoice(Long farmerId, Long ordersId, Date date) {
      // QInvoice in = QInvoice.invoice;
      // QPayment pay = QPayment.payment;
      // QOrders ord = QOrders.orders;
      //
      // Tuple result = jpaQueryFactory
      // .select(new CaseBuilder()
      // .when(ord.quotationId.isNotNull()).then(3)
      // .otherwise(5),
      // pay.paymentPrice)
      // .from(ord)
      // .join(pay).on(ord.paymentId.eq(pay.paymentId))
      // .where(ord.farmerId.eq(farmerId).and(ord.ordersId.eq(ordersId)))
      // .fetchOne();
      //
      // jpaQueryFactory.insert(in)
      // .set(in.farmerId, farmerId)
      // .set(in.orderId, ordersId)
      // .set(in.invoiceDate1, date)
      // .set(in.invoiceCommission, result.get(0, Long.class))
      // .set(in.invoicePrice, result.get(1, Long.class))
      // .execute();
      // }

      // 정산 내역 리스트
      public List<Payment> findOrdersIdAndDeliveryAndProductAndByDeliveryState(Long farmerId, String sDate,
                  String eDate,
                  String state, PageRequest pageRequest) throws ParseException {
            QPayment pay = QPayment.payment;

            // state를 String 클래스로 받아서 enum 클래스로 변환 후 Payment.state 비교
            PaymentStatus stateValue = null;
            if (state == "UNSETTLEMENT") {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송중
            else {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송완료

            // String으로 받은 날짜(sDate, eDate)를 payment의 invoiceDate와 비교를 위해 Data 형식으로 변환 작업
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = (Date) dateFormat.parse(sDate);
            Date endDate = (Date) dateFormat.parse(eDate);

            return jpaQueryFactory.selectFrom(pay)
                        .where(pay.farmerId.eq(farmerId)
                                    .and(pay.invoiceDate.between(startDate, endDate))
                                    .and(pay.state.eq(stateValue)))
                        .orderBy(pay.createDate.desc())
                        .offset(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .fetch();

      }

      // 정산 내역 리스트 수
      public Long invoiceCountByState(Long farmerId, String state) {
            QPayment pay = QPayment.payment;

            // Payment state의 값을 String 클래로 받아서 enum 형태로 변환 후 비교
            PaymentStatus stateValue = null;
            if (state == "UNSETTLEMENT") {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송중
            else {
                  stateValue = PaymentStatus.valueOf(state);
            } // 배송완료

            return jpaQueryFactory.select(pay.count())
                        .from(pay)
                        .where(pay.farmerId.eq(farmerId) // farmerId, state
                                    .and(pay.state.eq(stateValue)))
                        .fetchOne();
      }

}
