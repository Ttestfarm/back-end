package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class ProductImageFile {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer fileId;
	@Column
	private Integer productId;
	@Column
	private String thumbnail; // 대표 사진
	@Column
	private String directory;
	@Column
	private String size;
	
	// image 저장 방법 정하기
	@Column
	private String contentType; // 
	@Column
	private String uploadDate; // 
	@Column
	private String data;
	@Column
	@CreationTimestamp
	private Timestamp createDate;
	@Column
	private String state;
	
}
