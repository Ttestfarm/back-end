package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByFarmerId(Long FarmerId, PageRequest pageRequest);

}
