package com.kosta.farm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserEmail(String userEmail);
	// join에서 씀
  // boolean existsByUserEmail(String userEmail);
  // boolean existsByUserNickname(String userNickname);
}
