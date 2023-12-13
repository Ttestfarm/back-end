package com.kosta.farm.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegFarmerDto {
	private String farmName;
	private MultipartFile farmPixurl;
	private String farmTel;
	private String farmAddress;
	private String farmAddressDetail;
	private String registrationNum;
	private String farmBank;
	private String farmAccountNum;
	private String farmInterests;
}