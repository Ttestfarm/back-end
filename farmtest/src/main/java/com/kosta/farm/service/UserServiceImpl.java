package com.kosta.farm.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.repository.UserDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserDslRepository userDslRepository;
	private final ObjectMapper objectMapper;
}
