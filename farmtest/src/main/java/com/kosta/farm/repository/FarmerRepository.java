package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Integer> {

}
