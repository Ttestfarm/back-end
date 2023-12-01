package com.kosta.farm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kosta.farm.entity.Product;
import com.kosta.farm.repository.ProductRepository;

@SpringBootTest
class FarmtestApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	ProductRepository productRepository;
	
	@Test
	@DisplayName("상품 저장 테스트")
	public void createProductTest() {
		Product product= new Product();
		product.setProductName("테스트 상품");
		product.setProductPrice("100000");
		product.setProductDescription("상세설명");
		product.setProductStock("100");
		Product savedP= productRepository.save(product);
		System.out.println(savedP.toString());
		
	}
}
