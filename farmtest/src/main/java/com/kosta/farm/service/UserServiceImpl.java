package com.kosta.farm.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.farm.UserRole;
import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;

	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	// 회원가입
	@Override
	public void join(JoinRequestDto request) throws Exception {
		userRepository.save(User.builder()
				.userName(request.getUserName())
				.userEmail(request.getUserEmail())
				.userPassword(encoder.encode(request.getUserPassword()))
				.userRole(UserRole.USER)
				.build());
	}

	// 이메일 중복 체크
	@Override
	public boolean checkEmail(String userEmail) throws Exception {
		if (userRepository.existsByUserEmail(userEmail)) {
			throw new RuntimeException("중복된 이메일입니다.");
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

	@Override
	public void saveUser(User user) throws Exception {
		userRepository.save(user);
	}
}
