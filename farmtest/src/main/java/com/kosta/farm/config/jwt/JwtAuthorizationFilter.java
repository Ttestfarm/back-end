//package com.kosta.farm.config.jwt;
//
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.List;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.kosta.farm.entity.User;
//import com.kosta.farm.service.UserService;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
////인가 : 로그인 처리가 되야만 하는 요청이 들어왔을때 실행된다
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//	private final UserService userService;
//	@Autowired
//	private String secretKey;
//
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws ServletException, IOException {
//		Enumeration<String> headerNames = request.getHeaderNames();
//		while(headerNames.hasMoreElements()) {
//			String hname = headerNames.nextElement();
//			System.out.println(hname+":"+request.getHeader(hname));
//		}
//		
//		String header = request.getHeader("authorization");
//		System.out.println("header Authorization: " + header);
//
//		if (header == null || !header.startsWith("Bearer ")) {
//			chain.doFilter(request, response);
//			return;
//		}
//		String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
//
//		try {
//			// 토큰 검증
//			String userEmail = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token).getClaim("userEmail")
//					.asString();
//			if (userEmail != null) {
//				User user = userService.getLoginUserByUserEmail(userEmail);
//				Authentication authentication = new UsernamePasswordAuthenticationToken(user, "",
//						List.of(new SimpleGrantedAuthority(user.getUserRole().name())));
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//
//			}
//			chain.doFilter(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
//}