package com.kosta.farm.dto;

import com.kosta.farm.entity.Request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RequestWithQuotationCountDTO {
    private Request request;
    private Long quotationCount;

}
