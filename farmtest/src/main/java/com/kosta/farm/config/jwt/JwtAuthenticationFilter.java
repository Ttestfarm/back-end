package com.kosta.farm.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.config.auth.PrincipalDetails;
import com.kosta.farm.config.dto.LoginRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // login시 실행됨

	private final AuthenticationManager authenticationManager;

	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 진입");
		// request에 있는 userEmail과 userPassword를 파싱해서 자바 Object로 받기
		ObjectMapper om = new ObjectMapper(); 
		LoginRequestDto loginRequestDto = null;
		try {
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("JwtAuthenticationFilter : " + loginRequestDto);
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(loginRequestDto.getUserEmail(), loginRequestDto.getUserPassword());
		System.out.println("JwtAuthenticationFilter : 토큰생성완료");
		// authenticate 함수가 호출되면 인증 프로바이더가 유저 디테일 서비스의
		// loadUserByUsername(토큰의 첫번째 파라미터)를 호출하고
		// UserDetails를 리턴받아 토큰의 두번째 파라미터(credential)과
		// UserDetails(DB값)의 getPassword() 함수로 비교해서 동일하면
		// Authentication 객체를 만들어 필터체인으로 리턴해준다.
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println(principalDetails.getUser().getUserEmail());
		System.out.println(principalDetails.getUser().getUserPassword());
		System.out.println(principalDetails.getUser().getUserName());
		System.out.println(principalDetails.getUser().getUserNickname());
		System.out.println(principalDetails.getUser().getUserTel());
		System.out.println(principalDetails.getUser().getAddress1());
		System.out.println(principalDetails.getUser().getAddress2());
		System.out.println(principalDetails.getUser().getAddress3());
		System.out.println(principalDetails.getUser().getUserRoles());
		System.out.println(principalDetails.getUser().isUserState());
		
		return authentication;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		String jwtToken = JWT.create()
				.withSubject(principalDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
				.withClaim("id", principalDetails.getUser().getUserId())
				.withClaim("username", principalDetails.getUser().getUserEmail())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));
		
//		String refreshToken = JWT.create()
//				.withSubject(principalDetails.getUsername())
//				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
//				.withClaim("id", principalDetails.getUser().getUserId())
//				.withClaim("username", principalDetails.getUser().getUserEmail())
//				.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));
		
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
	}
}
