package com.kosta.farm.entity;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Orders { //order> orders로 대체 entity 생성 오류 때문에
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ordersId;
	// FK
	@Column
	private Long userId; //주문 회원
	@Column
	private Long farmerId;
	@Column
	private Long requestId;

	@Column
	private Long quotationId;
	@Column
	private Long paymentId;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	//주문 상태
	@Column
	@ColumnDefault("1")
	private String ordersState; // 0: 판매취소, 1: 결제완료, 2: 배송완료
	
	@Column
	private Long productId; //상품정보
	//상시판매를 위한 컬럼들
	@Column
	private Integer ordersCount; //주문 수량

	@Column
	private Integer ordersPrice; //주문 가격
	
//	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
//	private List<OrderReg> orderReg=new ArrayList<>();
//	}

}
