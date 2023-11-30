package com.kosta.farm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farmerfollow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer farmerFollowId; // entity를 쓰려면 id가 꼭 필요함
	@Column
	private Integer userId;
	@Column
	private Integer farmerId;
}
