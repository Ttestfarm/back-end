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
import com.kosta.farm.config.oauth2.PrincipalOAuth2UserService;
import com.kosta.farm.config.oauth2.OAuth2LoginSuccessHandler;
import com.kosta.farm.config.oauth2.PrincipalOAuth2UserService;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired
	private final UserService userService;

	@Autowired
	private PrincipalOAuth2UserService principalOauth2UserService;

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Autowired
	private CorsFilter corsFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.httpBasic().disable() // httpBasic 비활성화(header에 username, password를 암호화하지 않은 상태로 주고받기 때문)
				.addFilter(corsFilter) // 다른 도메인에서 접근 허용
				.csrf().disable() // csrf 공격 비활성화
				.formLogin().disable() // 폼 로그인 비활성화
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 비활성화
				.and()
				.addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class);

		http
				.oauth2Login()
				.authorizationEndpoint().baseUri("/oauth2/authorization") // 소셜 로그인 url
				.and()
				.redirectionEndpoint().baseUri("/oauth2/callback/*") // 소셜 인증 후 redirect url
				.and()
				.userInfoEndpoint().userService(principalOauth2UserService) // 회원 정보 처리
				.and()
				.successHandler(oAuth2LoginSuccessHandler);

		http
				// 인가(권한) 필요한 URL 지정
				.authorizeRequests()
				.antMatchers("/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/farmer/**").access("hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll(); // 나머지는 허용
		return http.build();
	}
}
