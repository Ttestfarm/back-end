package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserEmail(String userEmail);
	User findByUserId(Long userId);

	Boolean existsByUserEmail(String userEmail);
	User findByUserNameAndUserTel(String userName, String userTel);
	User findByUserNameAndUserEmail(String userName, String userEmail);
}
