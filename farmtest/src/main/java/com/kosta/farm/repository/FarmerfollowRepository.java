package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmerfollow;

public interface FarmerfollowRepository extends JpaRepository<Farmerfollow, Integer> {
	List<Farmerfollow> findByUserId(Integer userId);
}
