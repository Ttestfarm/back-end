package com.kosta.farm.dto;

import com.kosta.farm.entity.Farmer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerDto {
	private Long farmerId;
  private String farmName;
  private String farmPixurl;
  private String farmTel;
  private String farmAddress;
  private String farmAddressDetail;
  private String registrationNum;
  private String farmBank;
  private String farmAccountNum;
  private String farmInterest1;
  private String farmInterest2;
  private String farmInterest3;
  private String farmInterest4;
  private String farmInterest5;
  private Integer followCount;
  private Integer reviewCount;
  private Double rating;


  public Farmer toEntity() {
    return Farmer.builder()
    	.farmerId(farmerId)
      .farmName(farmName)
      .farmPixurl(farmPixurl)
      .farmTel(farmTel)
      .farmAddress(farmAddress)
      .farmAddressDetail(farmAddressDetail)
      .registrationNum(registrationNum)
      .farmBank(farmBank)
      .farmAccountNum(farmAccountNum)
      .farmInterest1(farmInterest1)
      .farmInterest2(farmInterest2)
      .farmInterest3(farmInterest3)
      .farmInterest4(farmInterest4)
      .farmInterest5(farmInterest5)
      .followCount(followCount)
      .reviewCount(reviewCount)
      .rating(rating)
      .build();
  }
}
