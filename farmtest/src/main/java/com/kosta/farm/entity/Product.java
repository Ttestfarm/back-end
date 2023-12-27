package com.kosta.farm.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String fileUrl; // 나머지 파일
	@Column
	private Long thumbNail; // 대표이미지 id
	@Column
	@ColumnDefault("sale")
<<<<<<< HEAD
	private String state; // 판매중(sale), 판매완료(soldout)
=======
	private String state; // 판매중, 판매완료 sale or soldout
>>>>>>> 18ea4149893abb82db640974587fe8b15f4b2bc3

	public void removeStock(Integer productStock) throws Exception {
		Integer restStock = this.productStock - productStock;
		if (restStock < 0) {
			throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + this.productStock);
		}
		this.productStock = restStock;

	}

}
