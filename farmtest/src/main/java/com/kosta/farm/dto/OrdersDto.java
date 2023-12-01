package com.kosta.farm.dto;

import lombok.Data;

@Data
public class OrdersDto {
	private Long orderId;
	private String product;
	private String quantity;
	private Integer price;
	private String name;
	private String tel;
	private String address;
}
