package com.kosta.farm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Integer> {
	// farmerId로 farmInterest 조회
	Farmer findByFarmerId(Long farmerId);
	
	//리뷰카운트순으로 파머리스트 가져오기
	List<Farmer> findByreviewCount(Integer reviewCount);
	
	//팜 이름으로 파머 리스트 가져오기
	List<Farmer> findByFarmName(String FarmName);
	
	
}
