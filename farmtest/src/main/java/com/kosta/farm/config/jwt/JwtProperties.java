package com.kosta.farm.config.jwt;

public interface JwtProperties {
	String SECRET_KEY = "pretty-farmers"; // 우리 서버 고유의 비밀키
	int EXPIRATION_TIME = 60000 * 24; // 24시간
	String HEADER_STRING = "Authorization";
	// String REFRESH_STRING = "Refresh";
}
