package com.kosta.farm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//@EnableBatchProcessing  // 배치 기능 활성화
@EnableScheduling      // 스케줄러 기능 활성화
@SpringBootApplication
public class FarmtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmtestApplication.class, args);
	}

}
