package com.kosta.farm.service;

import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;
import com.kosta.farm.util.UserRole;

import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;
	private final JavaMailSender javaMailSender;
	
	 @Value("${coolsms.apiKey}")
   private String apiKey;

   @Value("${coolsms.apiSecret}")
   private String apiSecret;

//	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, JavaMailSender javaMailSender) {
//		this.userRepository = userRepository;
//		this.encoder = encoder;
//		this.javaMailSender = javaMailSender;
//	}
	
	// 회원가입
	@Override
	public void join(JoinRequestDto request) throws Exception {
		String rawPassword = request.getUserPassword();
		String password = encoder.encode(rawPassword);
		userRepository.save(User.builder()
				.userName(request.getUserName())
				.userEmail(request.getUserEmail())
				.userPassword(password)
				.userRole(UserRole.ROLE_USER)
				.build());
	}

	// 이메일 중복 체크
	@Override
	public boolean checkEmail(String userEmail) throws Exception {
		// 중복되면 false 리턴, 안되면 true 리턴
		if (userRepository.existsByUserEmail(userEmail)) {
			return false;
		}
		return true;
	}

	// 로그인
	@Override
	public User login(LoginRequestDto request) throws Exception {
		User user = userRepository.findByUserEmail(request.getUserEmail());

		if (user == null) {
			throw new RuntimeException("해당하는 사용자를 찾을 수 없습니다.");
		}
		// 찾아온 User의 password와 입력된 password가 다르면 null return
		if (!encoder.matches(request.getUserPassword(), user.getUserPassword())) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}

		return user; 
	}

	// userId(서버 PK id)로 로그인한 유저 찾아오기 => 인증/인가에 사용
	@Override
	public User getLoginUserByUserId(Long userId) throws Exception {
		if (userId == null) {
			throw new IllegalArgumentException("userId가 없습니다.");
		}

		User user = userRepository.findByUserId(userId);

		if (user == null) {
			throw new RuntimeException("가입된 사용자가 없습니다.");
		}

		return user;
	}

	// userEmail로 로그인한 유저 찾아오기 => 인증/인가에 사용
	@Override
	public User getLoginUserByUserEmail(String userEmail) throws Exception {
		if (userEmail == null) {
			throw new IllegalArgumentException("userEmail이 없습니다.");
		}

		User user = userRepository.findByUserEmail(userEmail);

		if (user == null) {
			throw new RuntimeException("가입된 사용자가 없습니다.");
		}

		return user;
	}
	
	// 수정된 유저 정보 저장
	@Override
	public void saveUser(User user) throws Exception {
		userRepository.save(user);
	}
	
	// 이메일 찾기
	@Override
	public User findUserEmail(String userName, String userTel) throws Exception {
		return userRepository.findByUserNameAndUserTel(userName, userTel);
	}

	// 비밀번호 찾기
	@Override
	public User findUserPassword(String userName, String userEmail) throws Exception {
		return userRepository.findByUserNameAndUserEmail(userName, userEmail);
	}
	
	// 임시 비밀번호 생성
	@Override
	public String makeTempPassword() throws Exception {
		String tempPassword = RandomStringUtils.randomAlphanumeric(8);
		return tempPassword;
	}
	
	// 비밀번호 변경
	@Override
	public void updatePassword(Long userId, String newPassword) throws Exception {
    User user = userRepository.findByUserId(userId);
    user.setUserPassword(newPassword);
    userRepository.save(user);
	}
	

  public void sendEmail(String to, String subject, String body) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);

      javaMailSender.send(message);
  }
  
	// 임시 비밀번호 메일로 전송
	@Override
	public void sendTempPasswordEmail(String userEmail, String tempPassword) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(userEmail);
		message.setSubject("[Unpretty Farm] 임시 비밀번호 안내");
		message.setText("안녕하세요 Unpretty Farm 입니다.\n\n 임시 비밀번호는 다음과 같습니다: " + tempPassword);
		
		javaMailSender.send(message);
	}
	
	// 파머 등록 후 유저 정보 update
	@Override
	public void updateUserInfoAfterRegFarmer(User user, Long farmerId) throws Exception {
		if (user == null || farmerId == null) {
			throw new IllegalArgumentException("유효하지 않은 유저입니다.");
		}

		user.setFarmerId(farmerId);
		user.setUserRole(UserRole.ROLE_FARMER);

		saveUser(user);
	}

	@Override
	public void certifiedTelNumber(String telNumber, String certifyNumber) throws Exception {
		Message coolsms = new Message(apiKey, apiSecret);

		HashMap<String, String> params = new HashMap<>();
		params.put("to", telNumber);
		params.put("from", "01072822458");
		params.put("type", "SMS");
		params.put("text", "Unpretty Farm 휴대폰 인증번호는" + "[" + certifyNumber + "]" + "입니다.");
		params.put("app_version", "test app 1.2");

		try {
			JSONObject obj = (JSONObject) coolsms.send(params);
			System.out.println("SMS 전송 성공: " + obj.toString());
		} catch (CoolsmsException e) {
			System.out.println("SMS 전송 실패: " + e.getMessage());
			System.out.println("에러 코드: " + e.getCode());
		}

	}

	// 파머등록시 기본전화번호 체크하면 userTel 업데이트
	@Override
	public void updateUserTel(User user, String newTel) throws Exception {
		user.setUserTel(newTel);
		userRepository.save(user);
	}

}
