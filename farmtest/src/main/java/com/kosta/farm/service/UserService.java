package com.kosta.farm.service;

import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.entity.User;

public interface UserService {
  void join(JoinRequestDto request) throws Exception;
  boolean checkEmail(String userEmail) throws Exception;
  
  User login(LoginRequestDto request) throws Exception;
  User getLoginUserByUserId(Long userId) throws Exception;
  User getLoginUserByUserEmail(String userEmail) throws Exception;
}
