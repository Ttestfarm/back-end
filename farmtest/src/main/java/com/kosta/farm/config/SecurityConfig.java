package com.kosta.farm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

import com.kosta.farm.config.jwt.JwtAuthenticationFilter;
import com.kosta.farm.config.jwt.JwtAuthorizationFilter;
import com.kosta.farm.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CorsFilter corsFilter;

	@Autowired
	private UserRepository userRepository;

	@Bean // 패스워드 암호화
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilter(corsFilter) // 다른 도메인 접근 허용
		.csrf().disable() // csrf 공격 비활성화
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)	// session 비활성화
		.and()
		.formLogin().disable() // 폼 로그인 비활성화
		.httpBasic().disable() // httpBasic은 header에 userName, password를 암호화하지 않은 상태로 주고받음. 이를 사용하지 않겠다는 뜻.
		.addFilter(new JwtAuthenticationFilter(authenticationManager()))
		.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
	
		// 인가(권한) 필요한 URL 지정
		.authorizeRequests()
		.antMatchers("/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
    .antMatchers("/farmer/**").access("hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
    .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
    .anyRequest().permitAll(); // 나머지는 허용
	}

}
