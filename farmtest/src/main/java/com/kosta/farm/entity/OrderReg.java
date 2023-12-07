//package com.kosta.farm.entity;
//
//import java.sql.Timestamp;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//
//import lombok.Data;
//
//@Entity
//@Data
//public class OrderReg {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long orderRegId;
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "product_id")
//	private Product product;
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="orders_id")
//	private Orders orders;
//	@Column
//	private Integer orderPrice;
//	@Column
//	private Integer count;
//
//	public static OrderReg createOrder(Product product, Integer orderPrice, Integer count) {
//		OrderReg orderReg = new OrderReg();
//		orderReg.setProduct(product);
//		orderReg.setOrderPrice(orderPrice);
//		orderReg.setCount(count);
//		try {
//			product.removeStock(count);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return orderReg;
//
//	}
//}
