package com.kosta.farm.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.repository.PayInfoRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.util.QuotationStatus;
import com.kosta.farm.util.RequestStatus;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class PayService {
	private final PayInfoRepository payInfoRepository;
	private final ProductRepository productRepository;
	private final QuotationRepository quoteRepository;
	private final RequestRepository requestRepository;

	public void savepayInfo(PayInfo payInfo) throws Exception {
		payInfoRepository.save(payInfo);
		Product product = null;
		Quotation quote = null;

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

		if (payInfo.getQuotationId() != null) {
			quote = quoteRepository.findById(payInfo.getQuotationId()).get();
			quote.setState(QuotationStatus.COMPLETED);
			quoteRepository.save(quote);
		}
	}

	public void changeQuotationStatus(Long quotationId, QuotationStatus newStatus) {
		Quotation quotation = quoteRepository.findById(quotationId).orElse(null);
		if (quotation != null) {
			quotation.setState(newStatus);
			quoteRepository.save(quotation);

			if (newStatus == QuotationStatus.COMPLETED) {
				Long requestId = quotation.getRequestId();
				if (requestId != null) {
					Request request = requestRepository.findById(requestId).orElse(null);
					if (request != null) {
						request.setState(RequestStatus.MATCHED);
						requestRepository.save(request);
					}
				}
			}
		}
	}
}
