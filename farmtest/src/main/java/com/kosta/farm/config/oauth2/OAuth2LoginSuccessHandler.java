package com.kosta.farm.config.oauth2;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kosta.farm.config.auth.PrincipalDetails;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.expireTime}")
	private int expireTime;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		String jwtToken = JWT.create().withSubject(principalDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
				.withClaim("id", principalDetails.getUser().getUserId())
				.withClaim("username", principalDetails.getUser().getUserEmail()).sign(Algorithm.HMAC512(secretKey));

		response.setCharacterEncoding("UTF-8");
		String targetUrl = UriComponentsBuilder
				.fromUriString("http://localhost:3000/oauth/redirect/" + "Bearer " + jwtToken).build().toUriString();
		response.sendRedirect(targetUrl);
	}
}
