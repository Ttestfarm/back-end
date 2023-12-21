package com.kosta.farm.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDto {
	private Long userId;
	private Long requestId;
	private String requestProduct;
	private String requestQuantity;
	private Date requestDate;
	private String requestMessage;
	private Boolean choiceState; // 0 본인(기본 배송지), 1 선물

	private String name;
	private String tel;
	private String address1;
	private String address2;
	private String address3;
	private String requestState;
	private String userName;
	private Date createDate; // 자동

}
