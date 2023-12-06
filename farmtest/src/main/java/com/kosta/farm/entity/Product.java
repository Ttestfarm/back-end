package com.kosta.farm.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	// FK
	@Column
	private Long farmerId;
	@Column
	private Long categoryId;

	@Column
	private String productName; // 농산물명
	@Column
	private String productPrice; // 가격
	@Column
	private String productQuantity; // 판매 수량
	@Column
	private Integer productStock; // 재고
	@Column
	private String productDescription; // 상품 설명
	@Column
	private Integer ShippingCost; // 배송비
	@Column
	private Integer addShippingCost;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String productState; // 판매중, 매진, (판매취소)
	@Column
	private String fileUrl; // 나머지 파일
	@Column
	private Long thumbNail; // 대표이미지 id

//    private LocalDateTime regTime; //등록 시간
//    private LocalDateTime updateTime; //수정 시간

	public void removeStock(Integer orderQuantity) throws Exception {
		if (productStock < orderQuantity)
			throw new Exception("상품의 재고가 부족합니다");
		productStock -= orderQuantity; //상품 주문하면 재고 줄어드는 로직

	}

}
