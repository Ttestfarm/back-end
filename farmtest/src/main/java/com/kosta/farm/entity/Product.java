package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.kosta.farm.util.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String fileUrl; // 나머지 파일
	@Column
	private Long thumbNail; // 대표이미지 id
	@Column
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private ProductStatus state = ProductStatus.SALE; // 판매중(sale), 판매완료(soldout)

	public void removeStock(Integer productStock) throws Exception {
		Integer restStock = this.productStock - productStock;
		if (restStock < 0) {
			throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + this.productStock);
		}
		this.productStock = restStock;

	}

}
