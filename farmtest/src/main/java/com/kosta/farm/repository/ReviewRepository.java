package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.QFarmer;
import com.kosta.farm.entity.QReview;
import com.kosta.farm.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {


}
