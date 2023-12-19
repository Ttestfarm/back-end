package com.kosta.farm.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RegProductDto {
	private MultipartFile titleImage;
	private MultipartFile image1;
	private MultipartFile image2;
	private MultipartFile image3;
	private MultipartFile image4;
	private String name;
}
