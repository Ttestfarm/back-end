//package com.kosta.farm.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.filter.CorsFilter;
//
//import com.kosta.sec.config.jwt.JwtAuthenticationFilter;
//import com.kosta.sec.config.jwt.JwtAuthorizationFilter;
//import com.kosta.sec.repository.UserRepository;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//	@Autowired
//	private CorsFilter corsFilter;
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Bean // 메소드 있는 오브젝트를 ioc에 등록한다 패스워드 암호화
//	public BCryptPasswordEncoder paswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//
//				.addFilter(corsFilter) // CORS필터 추가 다른 도메인 접근 허용
//				.csrf().disable() // DISABLE CSRF 공격 비활성화

//				.sessionManagement() // 세션 관리 설정: 세션 비활성화 및 STATELESS로 세션 생성 정책 설정
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)// session비활성화

//				.and() // 그리고
//				.formLogin().disable() // 로그인 폼 사용 비활성화 HTTP Basic 인증 비활성화 HTTP 기본 인증 사용하지 않음
//				.httpBasic().disable() // httpbasic은 header에 username, password를 암호화 하지 않은 상태로 주고 받는다. 이를 사용하지 않겠다

//				// 요청에 대한 인가 규칙 설정
//				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
//				// verifying who it is confirms users are who they say they are
//				// 인증
//				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
//				// verifying what specific applications, files, and data a user has access to
//				// gives users permissionto access a resource
//				// 인가
//				.authorizeRequests() //
//				.antMatchers("/user/**")
//				.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//				.antMatchers("/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//				// "/manager/**" 패턴은 "ROLE_MANAGER" 혹은 ROLE_ADMIN 역할을 가진 사용자만 접근 가능
//				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//				// "/admin/**" 패턴은 "ROLE_ADMIN" 역할을 가진 사용자만 접근 가능
//				.anyRequest().permitAll(); // 나머지 모든 요청은 모든 사용자에게 허용됨
//
//	}
//
//}
