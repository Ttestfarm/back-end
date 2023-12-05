package com.kosta.farm.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuotDelDto {
	private Long farmerId;
	private List<Long> ids; 
}
