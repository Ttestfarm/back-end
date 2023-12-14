package com.kosta.farm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinRequestDto {
	private String userName;
	private String userEmail;
	private String userPassword;
}
