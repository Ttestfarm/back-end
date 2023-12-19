package com.kosta.farm.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.RegProductDto;

@RestController
public class TestController {
	
	@PostMapping("regproduct")
	public void regProduct(@ModelAttribute RegProductDto dto) {
		System.out.println("here");
		System.out.println(dto);
	}
}
