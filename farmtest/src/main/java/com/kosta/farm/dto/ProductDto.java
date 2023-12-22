package com.kosta.farm.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private Long productId;
	// FK
	private Long farmerId;
	private Long categoryId;

	private String productName; // 농산물명
	private String productPrice; // 가격
	private String productQuantity; // 판매 수량
	private Integer productStock; // 재고
	private String productDescription; // 상품 설명
	private Integer ShippingCost; // 배송비
	private Integer addShippingCost;
	private Long thumbNail; // 대표이미지 id
	private String fileUrl; // 나머지 파일
	private Date createDate;
	private String productState; // 판매중, 매진, (판매취소)
}
