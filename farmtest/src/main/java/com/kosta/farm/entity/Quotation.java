package com.kosta.farm.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
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
@DynamicInsert
@DynamicUpdate
public class Quotation {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer quotationId;
	@Column
	private Integer requestId;
	@Column
	private Integer farmerId;
	@Column
	private String quotationProduct;
	@Column
	private String quotationQuantity;
	@Column
	private String quotationPrice;
	@Column
	private String quotationComment;
	@Column
	private String quotationPicture;
	@CreationTimestamp
	@Column
	private Timestamp createDate; //자동
	@Column
	private Boolean state;
}
