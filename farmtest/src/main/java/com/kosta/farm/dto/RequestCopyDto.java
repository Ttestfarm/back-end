package com.kosta.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestCopyDto {
	private Long requestId; // 그 requestId
	private String requestProduct; // 요청 품목
	private String requestQuantity; // 요청 물량


}
