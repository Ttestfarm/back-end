package com.kosta.farm.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.repository.FarmDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {
	private final FarmDslRepository farmDslRepository;
	private final ObjectMapper objectMapper;
}
