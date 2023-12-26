package com.kosta.farm.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModifyFarmDto {
	private Long farmerId;
	private String farmName;
	private MultipartFile farmPixurl;
	private String farmTel;
	private String farmAddress;
	private String farmAddressDetail;
	private String registrationNum;
	private String farmBank;
	private String farmAccountNum;
	private String farmInterest;
	
	public ModifyFarmDto(Long farmerId, String farmName, MultipartFile farmPixurl, String farmTel, String farmAddress,
			String farmAddressDetail, String registrationNum, String farmBank, String farmAccountNum, String farmInterest) {
		this.farmerId = farmerId;
		this.farmName = farmName;
		this.farmPixurl = farmPixurl;
		this.farmTel = farmTel;
		this.farmAddress = farmAddress;
		this.farmAddressDetail = farmAddressDetail;
		this.registrationNum = registrationNum;
		this.farmBank = farmBank;
		this.farmAccountNum = farmAccountNum;
		this.farmInterest = farmInterest;
	}
	
	
}