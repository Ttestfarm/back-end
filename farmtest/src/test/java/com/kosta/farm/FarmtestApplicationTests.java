package com.kosta.farm;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//import com.kosta.farm.entity.Farmer;
//import com.kosta.farm.entity.Product;
//import com.kosta.farm.repository.FarmerRepository;
//import com.kosta.farm.repository.ProductRepository;
//import com.kosta.farm.service.FarmService;

@SpringBootTest
class FarmtestApplicationTests {

	//@Test
	void contextLoads() {
	}

//	try {
//	Map<String, Object> res = new HashMap<>();
//	List<Request> requestList = farmService.requestListByUser(userId); // requeststate이 request인것만 가져온다
//	List<RequestWithQuotationCountDTO> requestWithCountList = new ArrayList<>();
//	for (Request request : requestList) {
//		Long requestId = request.getRequestId(); // 각 요청의 id 가져오기
//		Long quoteCount = farmService.quoteCount(requestId); // 요청 id에 대한 견적 수 가져오기
//		RequestWithQuotationCountDTO requestWithCount = new RequestWithQuotationCountDTO();
//		requestWithCount.setRequest(request);
//		requestWithCount.setQuotationCount(quoteCount);
//		System.out.println(quoteCount);
//		requestWithCountList.add(requestWithCount);
//	}
//	Collections.sort(requestWithCountList, (r1, r2) -> {
//		Long count1 = r1.getQuotationCount();
//		Long count2 = r2.getQuotationCount();
//
//		// 견적 수 비교 후, 견적 수가 같으면 requestId 비교
//		if (count1 == null && count2 == null) {
//			return Long.compare(r2.getRequest().getRequestId(), r1.getRequest().getRequestId());
//		} else if (count1 == null) {
//			return 1;
//		} else if (count2 == null) {
//			return -1;
//		} else {
//			int countComparison = count2.compareTo(count1);
//			return countComparison != 0 ? countComparison
//					: Long.compare(r2.getRequest().getRequestId(), r1.getRequest().getRequestId());
//		}
//	});
//	res.put("requestWithCountList", requestWithCountList);
//	long endTime = System.currentTimeMillis(); // 요청 처리 종료 시간 측정
//	long duration = endTime - startTime; // 요청 처리 시간 계산
//	System.out.println("요청 처리 시간: " + duration + " 밀리초"); //66밀리초
//
//	return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//} catch (Exception e) {
//	e.printStackTrace();
//	return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//}

	
	
	

	
	
}
