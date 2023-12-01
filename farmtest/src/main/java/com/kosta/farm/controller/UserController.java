package com.kosta.farm.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.config.auth.PrincipalDetails;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("userInfo")
	public String user(Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println(principalDetails.getUser().getUserId());
		System.out.println(principalDetails.getUser().getUserEmail());
		System.out.println(principalDetails.getUser().getUserPassword());
		System.out.println(principalDetails.getUser().getUserRoles());
		return "userInfo";
	}
	
	@GetMapping("farmer/reports")
	public String reports() {
		return "reports";
	}
	
	@GetMapping("admin/users")
	public List<User> user() {
		return userRepository.findAll();
	}
	
	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
		user.setUserName(user.getUserName());
		user.setUserRoles("ROLE_USER");
		user.setUserState(true);
		userRepository.save(user);
		return "회원가입완료";
	}
}