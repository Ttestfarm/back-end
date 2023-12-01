package com.kosta.farm.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUserEmail(userEmail);
		if (userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return (UserDetails) new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
	}
}
