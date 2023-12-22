package com.kosta.farm.config.jwt;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kosta.farm.entity.User;
import com.kosta.farm.service.UserService;

import lombok.RequiredArgsConstructor;

//OncePerRequestFilter : 매번 들어갈 때 마다 체크 해주는 필터
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
	private final UserService userService;
	private final String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String hname = headerNames.nextElement();
			//System.out.println(hname + ":" + request.getHeader(hname));
		}

		String authorizationHeader = request.getHeader("authorization");
		// Header의 Authorization 값이 비어있으면 token을 전송하지 않음 (로그인 X)
		if (authorizationHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}
		// Header의 Authorization 값이 'Bearer '로 시작하지 않으면 잘못된 토큰
		if (!authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		// 전송받은 값에서 Bearer 뒷부분(token) 추출
		String token = authorizationHeader.split(" ")[1];
		// 전송받은 token이 만료되었으면 다음 필터 진행(인증 X)
		if (JwtTokenUtil.isExpired(token, secretKey)) {
			filterChain.doFilter(request, response);
			return;
		}

		// Jwt Token에서 userEmail 추출
		String userEmail = JwtTokenUtil.getUserEmail(token, secretKey);

		// 추출한 userEmail로 User 찾아오기
		User loginUser = null;
		try {
			loginUser = userService.getLoginUserByUserEmail(userEmail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// loginUser 정보로 UsernamePasswordAuthenticationToken 발급
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginUser.getUserEmail(), null, List.of(new SimpleGrantedAuthority(loginUser.getUserRole().name())));

		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		// 인증 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, "",
				List.of(new SimpleGrantedAuthority(loginUser.getUserRole().name())));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}