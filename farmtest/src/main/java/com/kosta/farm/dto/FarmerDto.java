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
    private String farmInterest;
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
            .farmInterest1(farmInterest)
            .rating(rating)
            .followCount(followCount)
            .reviewCount(reviewCount)
            .build();
    }
}
