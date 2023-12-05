package com.kosta.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Integer> {

}
