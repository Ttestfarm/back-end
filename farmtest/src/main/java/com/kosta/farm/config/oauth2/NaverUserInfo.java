//package com.kosta.farm.config.oauth2;
//
//import java.util.Map;
//
//public class NaverUserInfo implements OAuth2UserInfo {
//	
//	private Map<String,Object> attributes;
//	
//	public NaverUserInfo(Map<String,Object> attributes) {
//		this.attributes = attributes;
//	}
//	
//	@Override
//	public String getProviderId() {
//		return (String)attributes.get("id");
//	}
//
//	@Override
//	public String getProvider() {
//		return "naver";
//	}
//
//	@Override
//	public String getUserEmail() {
//		return (String)attributes.get("email");
//	}
//
//	@Override
//	public String getUserName() {
//		return (String)attributes.get("name");
//	}
//
//	@Override
//	public String getUserTel() {
//		return (String)attributes.get("mobile");
//	}
//}
