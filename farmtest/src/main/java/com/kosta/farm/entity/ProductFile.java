package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class ProductFile {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long productFileId;
//	@Column
//	private Long productId;
	@Column
	private String fileName;
	@Column
	private String directory;
	@Column
	private Long size;
	// image 저장 방법 정하기
//	@Column
//	private String uploadDate; // 
//	@Column
//	private String data;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String productState;

	
}
