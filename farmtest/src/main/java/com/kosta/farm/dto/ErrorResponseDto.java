package com.kosta.farm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponseDto {
	private String message;
  private String errorMessage;

	public ErrorResponseDto(String message, String errorMessage) {
		this.message = message;
		this.errorMessage = errorMessage;
	}
}
