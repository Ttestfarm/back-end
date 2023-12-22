package com.kosta.farm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//configuration (layout) - 구성
@Configuration
public class CorsConfig {
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 인증 정보 허용
		config.addAllowedOriginPattern("*"); // Access-Control-Allow-Origin (Response에 자동으로 추가해줌)
		config.addAllowedHeader("*"); // Access-Control-Allow-Headers
		config.addAllowedMethod("*"); // Access-Control-Allow-Method
		config.addExposedHeader("Authorization"); // 클라이언트에게 노출할 헤더를 설정 (JWT 토큰 브라우저 접근 허용)

		source.registerCorsConfiguration("/**", config); // 모든 경로에 config등록
		return new CorsFilter(source); // security 필터 체인에 추가
	}
}
