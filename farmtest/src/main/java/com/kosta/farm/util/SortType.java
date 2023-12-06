package com.kosta.farm.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
	FARMERID_ASC("아이디 오름차순") ,
	FARMERID_DESC("아이디 내림차순"),
	REVIEWCOUNT_ASC("리뷰 오름차순"),
	REVIEWCOUNT_DESC("리뷰 내림차순");
	private final String sorted;
	}
	
	
	

