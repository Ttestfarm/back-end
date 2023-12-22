package com.kosta.farm.controller;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.config.jwt.JwtTokenUtil;
import com.kosta.farm.config.oauth2.OAuth2LoginSuccessHandler;
import com.kosta.farm.dto.ErrorResponseDto;
import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.dto.ModifyFarmDto;
import com.kosta.farm.dto.ModifyUserDto;
import com.kosta.farm.dto.RegFarmerDto;
import com.kosta.farm.dto.UserInfoDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.FarmerService;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final FarmerService farmerService;

	private final BCryptPasswordEncoder encoder;

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.expireTime}")
	private int expireTime;

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody JoinRequestDto joinRequest) throws Exception {
		try {
			userService.checkEmail(joinRequest.getUserEmail());
			userService.join(joinRequest);
			return ResponseEntity.ok("회원가입에 성공했습니다.");
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

			String token = JwtTokenUtil.createToken(user.getUserEmail(), secretKey, expireTime);

			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization", "Bearer " + token);
			return ResponseEntity.ok().headers(headers).body("로그인 성공");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/user/userInfo")
	public ResponseEntity<?> userInfo(Authentication auth) throws Exception {
		User user = (User) auth.getPrincipal();
		try {
			User loginUser = userService.getLoginUserByUserEmail(user.getUserEmail());

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
					loginUser.getUserRole().name());

			return ResponseEntity.ok().body(userInfoResponse);
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("유저 정보 불러오기 실패", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	@PutMapping("/user/modify-user")
	public ResponseEntity<?> modifyUser(@RequestBody ModifyUserDto modifyUserRequest, Authentication auth) {
		try {
			// 로그인한 사용자 정보 가져오기
			User loginUser = (User) auth.getPrincipal();

			// 사용자 정보 수정
			loginUser.setUserName(modifyUserRequest.getUserName());
			loginUser.setUserPassword(encoder.encode(modifyUserRequest.getUserPassword()));
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
					loginUser.getUserRole().name());

			return ResponseEntity.ok().body(modifiedUserInfo);
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("회원 정보 수정 실패", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	@GetMapping("/modify-user/check-sms/{telNum}")
	public @ResponseBody String sendSMS(@PathVariable String telNum) {
		System.out.println(telNum);
		try {
			Random rand = new Random();
			String numStr = "";
			for (int i = 0; i < 6; i++) {
				String ran = Integer.toString(rand.nextInt(10));
				numStr += ran;
			}

			System.out.println("수신자 번호: " + telNum);
			System.out.println("인증번호: " + numStr);
			userService.certifiedTelNumber(telNum, numStr);
			return numStr;
		} catch (Exception e) {
			e.printStackTrace();
			return "인증번호 전송 오류가 발생했습니다.";
		}
	}

	@GetMapping("/find-email/{userName}/{userTel}")
	public ResponseEntity<?> findEmail(@PathVariable String userName, @PathVariable String userTel) {
		try {
			// UserService를 이용하여 userName과 userTel로 사용자 찾기
			User foundUser = userService.findUserEmail(userName, userTel);

			if (foundUser != null) {
				// 사용자를 찾았을 경우 이메일 응답
				return ResponseEntity.ok().body(foundUser.getUserEmail());
			} else {
				// 사용자를 찾지 못했을 경우
				return ResponseEntity.badRequest().body("해당 사용자를 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("이메일 찾기 실패", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	@GetMapping("/find-pw/{userName}/{userEmail}")
	public ResponseEntity<?> findPassword(@PathVariable String userName, @PathVariable String userEmail) {
		try {
			// UserService를 이용하여 userName과 userEmail로 사용자 찾기
			User foundUser = userService.findUserPassword(userName, userEmail);

			if (foundUser != null) {
				// 사용자를 찾았을 경우 임시 비밀번호 생성
				String tempPassword = userService.makeTempPassword();

				// 임시 비밀번호로 DB 업데이트
				userService.updatePassword(foundUser.getUserId(), tempPassword);

				// 이메일 전송
				userService.sendTempPasswordEmail(foundUser.getUserEmail(), tempPassword);
				return ResponseEntity.ok().body("임시 비밀번호가 이메일로 전송되었습니다.");
			} else {
				return ResponseEntity.badRequest().body("해당 사용자를 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("비밀번호 찾기 실패", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	@PostMapping("/findfarmer/reg-farmer")
	public ResponseEntity<String> regFarmer(
			@RequestPart("farmPixurl") MultipartFile farmPixurl,
			@RequestParam("farmName") String farmName,
			@RequestParam("farmTel") String farmTel,
			@RequestParam("farmAddress") String farmAddress,
			@RequestParam("farmAddressDetail") String farmAddressDetail,
			@RequestParam("registrationNum") String registrationNum,
			@RequestParam("farmBank") String farmBank,
			@RequestParam("farmAccountNum") String farmAccountNum,
			@RequestParam("farmInterest") String farmInterest,
			Authentication auth) throws Exception {

		try {
			User loginUser = (User) auth.getPrincipal();
			RegFarmerDto request = new RegFarmerDto();
			request.setFarmName(farmName);
			request.setFarmTel(farmTel);
			request.setFarmAddress(farmAddress);
			request.setFarmAddressDetail(farmAddressDetail);
			request.setRegistrationNum(registrationNum);
			request.setFarmBank(farmBank);
			request.setFarmAccountNum(farmAccountNum);
			request.setFarmInterest(farmInterest);
			request.setFarmPixurl(farmPixurl);

			Farmer registeredFarmer = farmerService.registerFarmer(request, farmPixurl);
			userService.updateUserInfoAfterRegFarmer(loginUser, registeredFarmer.getFarmerId());
			return ResponseEntity.ok("파머등록 성공");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("파머등록 실패: " + e.getMessage());
		}
	}

	// 팜 정보 수정
	@PutMapping("/farmer/modify-farm")
	public ResponseEntity<?> modifyFarm(
			@RequestPart("farmPixurl") MultipartFile farmPixurl,
			@RequestParam("farmName") String farmName,
			@RequestParam("farmTel") String farmTel,
			@RequestParam("farmAddress") String farmAddress,
			@RequestParam("farmAddressDetail") String farmAddressDetail,
			@RequestParam("registrationNum") String registrationNum,
			@RequestParam("farmBank") String farmBank,
			@RequestParam("farmAccountNum") String farmAccountNum,
			@RequestParam("farmInterest") String farmInterest,
			Authentication auth) throws Exception {

		try {
			User loginUser = (User) auth.getPrincipal();
			// 로그인한 유저의 farmerId 가져오기
			Long farmerId = loginUser.getFarmerId();

			if (farmerId == null) {
				// 로그인한 유저가 파머가 아닌 경우 에러 응답
				ErrorResponseDto errorResponse = new ErrorResponseDto("파머 정보가 없습니다", "파머로 등록된 사용자가 아닙니다");
				return ResponseEntity.status(400).body(errorResponse);
			}

			ModifyFarmDto request = new ModifyFarmDto();
			request.setFarmerId(farmerId);
			request.setFarmName(farmName);
			request.setFarmTel(farmTel);
			request.setFarmAddress(farmAddress);
			request.setFarmAddressDetail(farmAddressDetail);
			request.setRegistrationNum(registrationNum);
			request.setFarmBank(farmBank);
			request.setFarmAccountNum(farmAccountNum);
			request.setFarmInterest(farmInterest);
			request.setFarmPixurl(farmPixurl);

			Farmer modifiedFarmer = farmerService.modifyFarmer(request, farmPixurl);

			return ResponseEntity.ok("파머 정보가 성공적으로 수정되었습니다");
		} catch (NotFoundException e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("파머를 찾을 수 없습니다", e.getMessage());
			return ResponseEntity.status(404).body(errorResponse);
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("파머 정보 수정 실패", e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

}
