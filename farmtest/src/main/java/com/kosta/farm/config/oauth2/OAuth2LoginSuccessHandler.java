package com.kosta.farm.config.oauth2;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.config.auth.PrincipalDetails;
import com.kosta.farm.dto.ErrorResponseDto;
import com.kosta.farm.dto.UserInfoDto;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.UserService;

// 로그인 성공시 호출되는 핸들러
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.expireTime}")
	private int expireTime;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		String jwtToken = JWT.create()
				.withSubject(principalDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
				.withClaim("id", principalDetails.getUser().getUserId())
				.withClaim("username", principalDetails.getUser().getUserEmail()).sign(Algorithm.HMAC512(secretKey));
		// System.out.println(jwtToken);

		// 유저 정보를 가져오기
		User user = principalDetails.getUser();
		try {
			User loginUser = userService.getLoginUserByUserEmail(user.getUserEmail());

			// 유저 정보를 담은 DTO 생성
			UserInfoDto userInfoResponse = new UserInfoDto(
					loginUser.getUserId(),
					loginUser.getFarmerId(),
					loginUser.getUserName(),
					loginUser.getUserEmail(),
					loginUser.getUserPassword(),
					loginUser.getUserTel(),
					loginUser.getAddress1(),
					loginUser.getAddress2(),
					loginUser.getAddress3(),
					loginUser.getUserRole().name());

			String redirectUrl = UriComponentsBuilder
					.fromUriString("http://localhost:3000/oauth/redirect/" + "Bearer " + jwtToken).build().toUriString();
			response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			ErrorResponseDto errorResponse = new ErrorResponseDto("유저 정보 불러오기 실패", e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
			response.getWriter().flush();
		}
	}

}
