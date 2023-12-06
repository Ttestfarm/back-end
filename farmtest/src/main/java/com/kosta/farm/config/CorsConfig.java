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
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*"); // Access-Control-Allow-Origin (Response에 자동으로 추가해줌)
		config.addAllowedHeader("*"); // Access-Control-Allow-Headers
		config.addAllowedMethod("*"); // Access-Control-Allow-Method

		source.registerCorsConfiguration("/**", config); // config등록
		return new CorsFilter(source); // security에 등록해주려고
	}
}
