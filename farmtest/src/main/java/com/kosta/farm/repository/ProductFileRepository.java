package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.FileVo;

public interface ProductFileRepository extends JpaRepository<FileVo, Long> {

}
