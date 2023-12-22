package com.kosta.farm.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

	// token 발급
	public static String createToken(String userEmail, String secretKey, long expireTime) {
		// Claim = token에 들어갈 정보
		// Claim에 userEmail를 넣어 줌으로써 나중에 userEmail를 꺼낼 수 있음
		Claims claims = Jwts.claims();
		claims.put("username", userEmail);
		return Jwts.builder()
				.setHeaderParam("type", "JWT")
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(SignatureAlgorithm.HS256, secretKey.getBytes()).compact();
	}

	// Claims에서 userEmail 꺼내기
	public static String getUserEmail(String token, String secretKey) {

		return extractClaims(token, secretKey).get("username").toString();
	}

	// 발급된 Token이 만료 시간이 지났는지 체크
	public static boolean isExpired(String token, String secretKey) {
		Date expiredDate = extractClaims(token, secretKey).getExpiration();
		// Token의 만료 날짜가 지금보다 이전인지 체크
		return expiredDate.before(new Date());
	}

	// SecretKey를 사용해 Token Parsing
	private static Claims extractClaims(String token, String secretKey) {
		Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
		return claims;
	}
}