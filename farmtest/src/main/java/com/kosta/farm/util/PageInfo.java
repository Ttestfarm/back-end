package com.kosta.farm.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfo {
	private Integer allPage;
	private Integer curPage;
	private Integer startPage;
	private Integer endPage;

}