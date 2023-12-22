package com.kosta.farm.config.oauth2;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

	private Map<String,Object> attributes;
	
	public KakaoUserInfo(Map<String,Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getUserEmail() {
		return (String)(((Map<String,Object>)attributes.get("kakao_account")).get("email"));
		// return (String)attributes.get("account_email");
	}

	@Override
	public String getUserName() {
		return (String)(((Map<String, Object>)attributes.get("properties")).get("nickname"));
	}

	@Override
	public String getUserTel() {
		return null;
	}

}
