package com.kosta.farm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.kosta.farm.config.jwt.JwtTokenFilter;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserService userService;

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Autowired
	private CorsFilter corsFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.httpBasic().disable() // httpBasic 비활성화(header에 username, password를 암호화하지 않은 상태로 주고받기 때문)
				.addFilter(corsFilter) // 다른 도메인에서 접근 허용
				.csrf().disable() // csrf 공격 비활성화
				.formLogin().disable() // 폼 로그인 비활성화
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 비활성화
				.and()
				.addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)

				// 인가(권한) 필요한 URL 지정
				.authorizeRequests()
				.antMatchers("/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/farmer/**").access("hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll() // 나머지는 허용
				.and().build();
	}
}
