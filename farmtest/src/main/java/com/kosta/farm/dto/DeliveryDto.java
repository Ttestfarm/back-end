package com.kosta.farm.dto;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Data
public class DeliveryDto {
	private Long deliveryId;
	private Long ordersId; // 주문 번호 
	private String tCode; // 택배사 코드
	private String tName; // 택배사 이름
	private String tInvoice; // 송장 번호
	private String deliveryState; // 0: 오류, 1: 배송중, 2: 배송 완료
	
	// 추가 정보
	private String product; // 품목
	private String quantity; // 수량
	private Integer price; // 가격
	private String address; // 주소
}