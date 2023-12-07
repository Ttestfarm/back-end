package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmerfollow;

public interface FarmerfollowRepository extends JpaRepository<Farmerfollow, Long> {
	List<Farmerfollow> findByUserId(Long userId);
}
