package com.kosta.farm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.User;

public interface PayInfoRepository extends JpaRepository<PayInfo, String> {
	PayInfo findByUserId(Long userId);
	   
	List<PayInfo> findAllByUserId(Long userId);

    Optional<PayInfo> findByReceiptIdAndUserId(String ReceiptId, Long userId);
    
    List<PayInfo> findPayInfoByUserId(Long userId);
}
