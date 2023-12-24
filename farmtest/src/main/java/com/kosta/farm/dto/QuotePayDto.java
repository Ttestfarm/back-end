package com.kosta.farm.dto;

import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuotePayDto {
	private Quotation quotation;
	private Request request;
	
	 public QuotePayDto(Quotation quotation, Request request) {
	        this.quotation = quotation;
	        this.request = request;
	    }
}
