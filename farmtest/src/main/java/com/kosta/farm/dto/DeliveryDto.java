package com.kosta.farm.dto;

import lombok.Data;

@Data
public class DeliveryDto {
	private Long ordersId;
	private String tCode;
	private String tInvocie;
	private String product;
	private String deliveryState;
}
