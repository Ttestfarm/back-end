package com.kosta.farm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.service.FarmerService;

@RestController
public class FarmerController {
	@Autowired
	private FarmerService farmerService;
}
