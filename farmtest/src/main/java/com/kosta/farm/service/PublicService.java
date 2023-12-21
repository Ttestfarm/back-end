package com.kosta.farm.service;

import java.io.OutputStream;
import java.util.List;

import com.kosta.farm.dto.CompanyDto;

public interface PublicService {
	public void readImage(Integer num, OutputStream out) throws Exception;
	
	// 택배 API
	public List<CompanyDto> requestCompanyList() throws Exception;
}
