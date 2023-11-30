package com.kosta.farm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.service.UserService;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
}
