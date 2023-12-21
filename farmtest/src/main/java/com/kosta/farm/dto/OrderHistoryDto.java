package com.kosta.farm.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Review;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FarmerfollowRepository;
import com.kosta.farm.repository.OrdersRepository;
import com.kosta.farm.repository.PaymentRepository;
import com.kosta.farm.repository.ProductFileRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.repository.ReviewRepository;
import com.kosta.farm.repository.UserRepository;
import com.kosta.farm.service.UserService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OrderHistoryDto {
   // 주문정보
   private Orders orders;
   // 리뷰정보
   private Review review;
   // 상품정보
   private ProductInfoDto productInfo;
   private QuotationInfoDto quotationInfo;

   public Review getReview() {
      return this.review;
   }

   //
   // private Product productInfo;
   // public Product getProductInfo() {
   // return this.productInfo;
   // }
}
