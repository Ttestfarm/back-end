package com.kosta.farm.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.repository.PayInfoRepository;
import com.kosta.farm.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class PayService {
	private final PayInfoRepository payInfoRepository;
	private final ProductRepository productRepository;

	public void savepayInfo(PayInfo payInfo) throws Exception {
		payInfoRepository.save(payInfo);
		Product product = null;
		if (payInfo.getQuotationId() == null) {
			product = productRepository.findById(payInfo.getProductId()).get();
			Integer currentStock = product.getProductStock();
			if (currentStock == (null) || (currentStock < payInfo.getCount())) {
				throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + currentStock);
			} else {
				product.setProductStock(currentStock - payInfo.getCount());
				productRepository.save(product);
			}
		}
	}
}
