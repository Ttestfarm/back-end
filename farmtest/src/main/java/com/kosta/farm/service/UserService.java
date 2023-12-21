package com.kosta.farm.service;

import com.kosta.farm.dto.JoinRequestDto;
import com.kosta.farm.dto.LoginRequestDto;
import com.kosta.farm.entity.User;

public interface UserService {

	void join(JoinRequestDto request) throws Exception;

	User login(LoginRequestDto request) throws Exception;

	User getLoginUserByUserId(Long userId) throws Exception;

	User getLoginUserByUserEmail(String userEmail) throws Exception;

	void saveUser(User user) throws Exception;

	User findUserEmail(String userName, String userTel) throws Exception;

	User findUserPassword(String userName, String userEmail) throws Exception;

	String makeTempPassword() throws Exception;
	
	public boolean checkEmail(String userEmail) throws Exception;
	
	void updatePassword(Long userId, String newPassword) throws Exception;

	void sendTempPasswordEmail(String userEmail, String tempPassword) throws Exception;

	void updateUserInfoAfterRegFarmer(User user, Long farmerId) throws Exception;

	void certifiedTelNumber(String telNumber, String certifyNumber) throws Exception;

	void updateUserTel(User user, String newTel) throws Exception;
}
