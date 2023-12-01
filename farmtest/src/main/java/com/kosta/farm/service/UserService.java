//package com.kosta.farm.service;
//
//import java.util.Optional;
//
//import javax.transaction.Transactional;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.kosta.farm.entity.User;
//import com.kosta.farm.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    private final BCryptPasswordEncoder encoder;
//
//    // 이메일 중복체크 - 중복이면 true 리턴
//    public boolean checkUserEmailDuplicate(String userEmail) {
//      return userRepository.existsByUserEmail(userEmail);
//    }
//
//    // 이메일 중복체크 - 중복이면 true 리턴
//    public boolean checkUserNicknameDuplicate(String userNickname) {
//      return userRepository.existsByUserNickname(userNickname);
//    }
//
//    public void join(JoinRequest req) {
//      userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
//    }
//
//		public User login(LoginRequest request) {
//			User user = userRepository.findByLoginId(request.getLoginId());
//
//			// loginId와 일치하는 User가 없으면 null return
//			if (user.isEmpty()) {
//				return null;
//			}
//
//			User user = user.get();
//
//			// 찾아온 User의 password와 입력된 password가 다르면 null return
//			if (!user.getUserPassword().equals(request.getUserPassword())) {
//				return null;
//			}
//
//			return user;
//		}
//}