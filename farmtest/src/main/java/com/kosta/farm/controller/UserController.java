package com.kosta.farm.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.config.jwt.JwtTokenUtil;
import com.kosta.farm.dto.ErrorResponseDto;
import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.dto.ModifyUserDto;
import com.kosta.farm.dto.UserInfoDto;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.expireTime}")
  private int expireTime;

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
				return ResponseEntity.status(401).body("{\"message\": \"이메일 또는 비밀번호가 틀렸습니다.\"}");
			}

			String token = JwtTokenUtil.createToken(user.getUserEmail(), secretKey, expireTime);
			//System.out.println(token);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + token);
			// System.out.println(user.isUserState());
			return ResponseEntity.ok().headers(headers).body("로그인 성공");
			// return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\"message\": \"이메일 중복 확인 실패: " + e.getMessage() + "\"}");
			// return ResponseEntity.badRequest().body("이메일 중복 확인 실패: " + e.getMessage());
		}
	}
	
	@GetMapping("/login/userInfo")
  public ResponseEntity<?> userInfo(Authentication auth) throws Exception {
		try {
			User loginUser = userService.getLoginUserByUserEmail(auth.getName());

	    UserInfoDto userInfoResponse = new UserInfoDto(
	            loginUser.getUserId(),
	            loginUser.getFarmerId(),
	            loginUser.getUserName(),
	            loginUser.getUserEmail(),
	            loginUser.getUserPassword(),
	            loginUser.getUserTel(),
	            loginUser.getAddress1(),
	            loginUser.getAddress2(),
	            loginUser.getAddress3(),
	            loginUser.getUserRole().name()
	    );
	    
	    return ResponseEntity.ok().body(userInfoResponse);
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("유저 정보 불러오기 실패", e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
		}
	}
	
	@PutMapping("/mypage/modify-user")
	public ResponseEntity<?> modifyUser(@RequestBody ModifyUserDto modifyUserRequest, Authentication auth) {
	    try {
	        // 로그인한 사용자 정보 가져오기
	        User loginUser = userService.getLoginUserByUserEmail(auth.getName());

	        // 사용자 정보 수정
	        loginUser.setUserName(modifyUserRequest.getUserName());
	        loginUser.setUserPassword(modifyUserRequest.getUserPassword());
	        loginUser.setUserTel(modifyUserRequest.getUserTel());
	        loginUser.setAddress1(modifyUserRequest.getAddress1());
	        loginUser.setAddress2(modifyUserRequest.getAddress2());
	        loginUser.setAddress3(modifyUserRequest.getAddress3());

	        // 수정된 사용자 정보 저장
	        userService.saveUser(loginUser);

	        // 수정된 사용자 정보 응답
	        UserInfoDto modifiedUserInfo = new UserInfoDto(
	                loginUser.getUserId(),
	                loginUser.getFarmerId(),
	                loginUser.getUserName(),
	                loginUser.getUserEmail(),
	                loginUser.getUserPassword(),
	                loginUser.getUserTel(),
	                loginUser.getAddress1(),
	                loginUser.getAddress2(),
	                loginUser.getAddress3(),
	                loginUser.getUserRole().name()
	        );

	        return ResponseEntity.ok().body(modifiedUserInfo);
	    } catch (Exception e) {
	        ErrorResponseDto errorResponse = new ErrorResponseDto("회원 정보 수정 실패", e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}
}
