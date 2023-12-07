package com.kosta.farm.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.config.jwt.JwtTokenUtil;
import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody JoinRequestDto joinRequest) throws Exception {
		try {
			userService.checkEmail(joinRequest.getUserEmail());
			userService.join(joinRequest);
			return ResponseEntity.ok("회원가입 성공");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
		}
	}

	@PostMapping("/join/check-email")
	public ResponseEntity<String> checkEmail(@RequestBody Map<String, Object> userEmail) throws Exception {
		try {
			String inputEmail = (String) userEmail.get("userEmail");
			
			if (userService.checkEmail(inputEmail)) {
				return ResponseEntity.ok("사용 가능한 이메일입니다.");
			}
			return ResponseEntity.status(409).body("중복된 이메일입니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) throws Exception {
		try {
			User user = userService.login(loginRequest);
			if (user == null) {
				return ResponseEntity.status(401).body("이메일 또는 비밀번호가 틀렸습니다.");
			}
			
			String secretKey = "pretty-farmers";
			int expireTime = 60000 * 24; // 24시간

			String token = JwtTokenUtil.createToken(user.getUserEmail(), secretKey, expireTime);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authrozation", "Bearer " + token);
			
			return ResponseEntity.ok().headers(headers).body("로그인 성공");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("이메일 중복 확인 실패: " + e.getMessage());
		}
	}
	
}
