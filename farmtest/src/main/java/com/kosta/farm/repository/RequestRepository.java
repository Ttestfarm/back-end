package com.kosta.farm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.Request;
import com.kosta.farm.util.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {
	// requestProduct = farminterest인 요청서 리스트 (파머스페이지 요청서 보기에 사용)
	List<Request> findRequestByRequestProduct(String farmInterest);

	// 유저아이디별로 리퀘스트목록 보기
	List<Request> findRequestByUserId(Long userId);

	List<Request> findByState(RequestStatus state);
	
	List<Request> findRequestByUserIdAndStateOrderByRequestIdDesc(Long userId, RequestStatus state);

	List<Request> findByCreateDateBeforeAndStateNot(LocalDateTime oneWeekAgo, RequestStatus expired);
	
	
}
