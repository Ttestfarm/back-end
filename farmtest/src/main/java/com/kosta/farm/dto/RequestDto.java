package com.kosta.farm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDto {
	private Long userId;
	private String requestProduct;
	private String requestQuantity;
	private String requestDate;
	private String requestMessage;
	private String address;
	private String tel;
	private String requestState;
}
