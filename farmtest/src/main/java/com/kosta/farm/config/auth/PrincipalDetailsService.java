package com.kosta.farm.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kosta.farm.entity.User;
import com.kosta.farm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUserEmail(username);
    // System.out.println(user);

    if (user == null) {
      throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
    }

    return new PrincipalDetails(user);
	}
}
