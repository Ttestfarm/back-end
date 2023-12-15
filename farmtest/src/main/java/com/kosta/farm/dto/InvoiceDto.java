package com.kosta.farm.dto;

import lombok.Data;

@Data
public class InvoiceDto {
	private Integer commission;
	private String date1;
	private String date2;
	private String price;
}
