package com.kosta.farm.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {
	
	private final FarmerRepository farmerRepository;
	private final FarmerDslRepository farmerDslRepository;
	private final ObjectMapper objectMapper;
}
