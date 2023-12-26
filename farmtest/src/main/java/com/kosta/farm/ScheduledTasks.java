package com.kosta.farm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss:SSS");
	
//	@Scheduled(fixedRate = 1000) //1초에 한번씩 하겠따
//	public void fixedRate() throws InterruptedException {
//		log.info("1 시작");
//		TimeUnit.SECONDS.sleep(5);
//		log.info("1 fixedRate: 현재시간 - {}", formatter.format(LocalDateTime.now()));
//	}
}
