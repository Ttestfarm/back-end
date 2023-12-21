package com.kosta.farm.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.FarmerDto;
import com.kosta.farm.dto.FarmerInfoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Farmer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long farmerId;
	@Column
	private String farmName; // 팜 이름
	@Column
	private String farmPixurl; // 팜 사진 파일경로
	@Column
	private String farmTel; // 팜 전화번호
	@Column
	private String farmAddress; // 팜 주소1
	@Column
	private String farmAddressDetail; // 팜 주소2
	@Column
	private String registrationNum; // 사업자등록번호
	@Column
	private String farmBank; // 팜 계좌 은행
	@Column
	private String farmAccountNum; // 팜 계좌번호
	@Column(nullable = true)
	private String farmInterest1; // 관심 농산물
	@Column(nullable = true)
	private String farmInterest2;
	@Column(nullable = true)
	private String farmInterest3;
	@Column(nullable = true)
	private String farmInterest4;
	@Column(nullable = true)
	private String farmInterest5;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Builder.Default
	private boolean farmerState = true; // 0 탈퇴 , 1 계정유지
	@Column
	@ColumnDefault("0")
	private Integer followCount;
	@Column
	@ColumnDefault("0")
	private Integer reviewCount;
	@Column
	private Double rating;

	public void updateAvgRating(List<Review> reviews) {
		if (reviews == null || reviews.isEmpty()) {
			this.rating = 0.0;
			return;
		}
		double totalRating = 0.0;
		for (Review review : reviews) {
			totalRating += review.getRating();
		}
		this.rating = totalRating / reviews.size();
	}

	public FarmerInfoDto toDto() {
		StringBuilder farmInterestBuilder = new StringBuilder();
		addFarmInterest(farmInterestBuilder, farmInterest1);
		addFarmInterest(farmInterestBuilder, farmInterest2);
		addFarmInterest(farmInterestBuilder, farmInterest3);
		addFarmInterest(farmInterestBuilder, farmInterest4);
		addFarmInterest(farmInterestBuilder, farmInterest5);
		String farmInterest = farmInterestBuilder.toString();

		return FarmerInfoDto.builder().farmerId(farmerId).farmName(farmName).farmPixurl(farmPixurl).farmAddress(farmAddress)
	            .farmInterest(farmInterest)
				.followCount(followCount).reviewCount(reviewCount).rating(rating).build();
	}

	private void addFarmInterest(StringBuilder builder, String interest) {
		if (interest != null && !interest.isEmpty()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(interest);
		}
	}
}