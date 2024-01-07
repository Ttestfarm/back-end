package com.kosta.farm.config.oauth2;

public interface OAuth2UserInfo {
	String getProviderId();
	String getProvider();
	String getUserEmail();
	String getUserName();
	String getUserTel();
}