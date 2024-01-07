package com.kosta.farm.dto;

import lombok.Data;

@Data
public class PaymentInfoDto {
	private String receiptId; // 결제번호
	private String productName;
	private Integer quantity;
	
}
