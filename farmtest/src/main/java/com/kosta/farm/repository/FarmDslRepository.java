package com.kosta.farm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.kosta.farm.entity.Farmer;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class FarmDslRepository {
	@Autowired
	private JPAQueryFactory jpaQueryFactory;

	// 농부 리스트 가져오기
	public Page<Farmer> getFarmersByPage(Integer page, Integer size) {
		PageRequest pageRequest= PageRequest.of(page, size);
		return null;
	}

}
