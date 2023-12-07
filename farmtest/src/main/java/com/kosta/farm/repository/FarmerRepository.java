package com.kosta.farm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.util.PageInfo;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
	// 리뷰카운트순으로 파머리스트 가져오기
	List<Farmer> findByreviewCount(Integer reviewCount);

//	List<Farmer> findByRating(Long rating, Pageable pageable);
	// 팜 이름으로 파머 리스트 가져오기
//	List<Farmer> findByFarmName(String farmName);
	List<Farmer> findByFarmName(String farmName, Pageable pageable);

	// 매개변수를 활용한 정렬
	Page<Farmer> findByfarmNameContaining(String searchKeyword, Pageable pageable);

	// 파머이름 포함한

	// 파머 팜 인터레스트 123 포함한
	Page<Farmer> findByFarmInterest1Contains(String farmInterest1, PageRequest pageRequest);

	Page<Farmer> findByFarmInterest2Contains(String farmInterest2, PageRequest pageRequest);

	Page<Farmer> findByFarmInterest3Contains(String farmInterest3, PageRequest pageRequest);

	Page<Farmer> findByFarmInterest1ContainingOrFarmInterest2ContainingOrFarmInterest3Containing(String farmInterest1,
			String farmInterest2, String farmInterest3, PageRequest pageRequest);

	List<Farmer> findByfollowCount(Integer followCount, Sort sort);

	Page<Farmer> findPageBy(Pageable pageable);

	Slice<Farmer> findSliceBy(Pageable pageable);

}
