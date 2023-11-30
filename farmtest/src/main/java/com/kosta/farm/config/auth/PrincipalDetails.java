package com.kosta.farm.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kosta.farm.entity.User;

public class PrincipalDetails implements UserDetails {
	private User user;
	
	public PrincipalDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collect = new ArrayList<>();
    collect.add(new GrantedAuthority() {
       @Override
       public String getAuthority() {
          return user.getUserRoles();
       }
    });
    return collect;
	}

	@Override
  public String getPassword() {
     return user.getUserPassword();
  }

  @Override
  public String getUsername() {
     return user.getUserEmail(); // 로그인을 이메일로 함
  }

  @Override
  public boolean isAccountNonExpired() { // 계정 만료 여부
     return true;
  }

  @Override
  public boolean isAccountNonLocked() { // 계정 잠금 여부
     return true;
  }

  @Override
  public boolean isCredentialsNonExpired() { // 비밀번호 만료 여부
     return true;
  }

  @Override
  public boolean isEnabled() { // 계정 활성화 여부
     return true;
  }
}
