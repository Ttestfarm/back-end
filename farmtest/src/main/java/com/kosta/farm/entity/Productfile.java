package com.kosta.farm.entity;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Productfile {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer fileId;
	@Column
	private String fileName;
	@Column
	private String directory;
	@Column
	private Long size;
	@Column
	@CreationTimestamp 
	private Timestamp createDate;
	@Column
	private String fileState;
	
}
