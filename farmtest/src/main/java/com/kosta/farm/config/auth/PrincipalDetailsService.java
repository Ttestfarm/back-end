package com.kosta.farm.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;

public class PrincipalDetailsService implements UserDetailsService {
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(userEmail);
		if (userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}
}
