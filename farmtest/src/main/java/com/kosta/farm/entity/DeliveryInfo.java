package com.kosta.farm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long deliveryInfoId;
	@Column
	private Long paymentId;
	@Column
	private String name;
	@Column
	private String infoAddress;
	@Column
	private String infoTel;
}
