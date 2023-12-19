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
public class FileVo {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long FileId;
	@Column
	private String fileName;
	@Column
	private String directory;
	@Column
	private Long size;
	// image 저장 방법 정하기
	@Column
	@CreationTimestamp
	private Timestamp createDate;
}
