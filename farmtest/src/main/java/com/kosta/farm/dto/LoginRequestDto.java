package com.kosta.farm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDto {
	private String userEmail;
	private String userPassword;
}
