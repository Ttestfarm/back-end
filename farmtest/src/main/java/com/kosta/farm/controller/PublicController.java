package com.kosta.farm.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.CompanyDto;
import com.kosta.farm.service.PublicService;

@RestController
public class PublicController {
	
	@Autowired
	private PublicService publicService;
	
	// img 불러오기
	@GetMapping("/img/{num}")
	public void imageView(@PathVariable Integer num, HttpServletResponse response) {
		try {
			publicService.readImage(num, response.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 택배사 정보 제공
	@GetMapping("/companylist")
	public ResponseEntity<List<CompanyDto>> companyList() {
		Long farmerId = (long) 1;
		try {
			List<CompanyDto> comList = publicService.requestCompanyList();
			return new ResponseEntity<List<CompanyDto>>(comList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<CompanyDto>>(HttpStatus.BAD_REQUEST);
		}
	}
}
