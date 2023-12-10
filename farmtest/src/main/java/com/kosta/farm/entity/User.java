package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kosta.farm.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId; // 서버에서 사용하는 userId
	@Column
	private Long farmerId;
	@Column
	private String userName; // 서비스에서 사용할 이름 (소셜로그인의 경우 닉네임 전달받음)
	@Column
	private String userEmail; // 로그인 ID
	@Column
	private String userPassword;
	@Column
	private String userTel;
	@Column
	private String address1;
	@Column
	private String address2;
	@Column
	private String address3;
	@Column
	private String provider;
	@Column
	private String providerId;
	@Column
	@Enumerated(EnumType.STRING)
	private UserRole userRole; // USER, FARMER, ADMIN
	@CreationTimestamp
	@Column
	private Timestamp createDate;
	@Column(columnDefinition = "BOOLEAN DEFAULT true")
	private boolean userState; // 탈퇴: false, 유효한 유저: true
	// error : 스키마도 1로 잘 설정되어있는데 0으로 들어감

}
