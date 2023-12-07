//package com.kosta.farm.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kosta.farm.dto.OrdersDto;
//import com.kosta.farm.dto.QuotDelDto;
//import com.kosta.farm.dto.QuotationDto;
//import com.kosta.farm.entity.Quotation;
//import com.kosta.farm.entity.Request;
//import com.kosta.farm.service.FarmerService;
//import com.kosta.farm.unti.PageInfo;
//
//@RestController
//@RequestMapping("/farmer")
//public class FarmerController {
//	@Autowired
//	private FarmerService farmerService;
//	
//	// 팜 정보 관리
//	
//	// 매칭 주문 요청서 보기
//	// 관심 농산물인 요청서 리스트 보기
//	@GetMapping("/requestlist/{farmerId}/{farmInterest}")
//	public ResponseEntity<List<Request>> requestList(@PathVariable Long farmerId,
//			@PathVariable String farmInterest) {
//		try {
//			List<Request> reqList = farmerService.findRequestsByFarmInterest(farmerId ,farmInterest);
//			return new ResponseEntity<List<Request>>(reqList, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<List<Request>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 견적서 보내기
//	@PostMapping("/regquot")
//	public ResponseEntity<String> regQuotation(@RequestBody Quotation quot) {
//		try {
//			return new ResponseEntity<String>("성공" ,HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 견적 현황 페이지
//	// 견적서 상태로(0 : 견적서 취소, 1 : 대기중, 2 : 기간 만료, 3 : 결제완료) 견적서 리스트 보여주기
//	@GetMapping("/quotstate/{page}/{farmerId}/{state}")
//	public ResponseEntity<Map<String, Object>> quotList(@PathVariable Integer page, 
//			@PathVariable Long farmerId, @PathVariable String state) {
//		try {
//			PageInfo pageInfo = new PageInfo(page);
//			List<QuotationDto> quotList = farmerService.findQuotationByFarmerIdAndStateAndPage(farmerId, state, pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("pageInfo", pageInfo);
//			res.put("quotList", quotList);
//			return new ResponseEntity<Map<String,Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 견적서 취소
//	@PatchMapping("/quotdelete")
//	public ResponseEntity<String> quotdelete(@RequestBody QuotDelDto dto) {
//		try {
//			farmerService.updateQuotationByFarmerIdAndRequestIds(dto.getFarmerId(), dto.getIds());
//			return new ResponseEntity<String>(HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 견적서 상세보기
//	@GetMapping("/quotdetail")
//	public ResponseEntity<Quotation> quotdetail(@PathVariable Long quotId) {
//		try {
//			Quotation quot = farmerService.findQuotationByQuotationId(quotId);
//			return new ResponseEntity<Quotation>(quot, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Quotation>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 결제 완료 페이지
//	@GetMapping("/orderlist/{page}/{farmerId}/{type}")
//	public ResponseEntity<Map<String, Object>> orderList(@PathVariable Integer page, 
//			@PathVariable Long farmerId, @PathVariable String type) {
//		try {
//			PageInfo pageInfo = new PageInfo(page);
//			List<OrdersDto> orderList = farmerService.findOrdersByFarmerIdAndPage(farmerId, type, pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("pageInfo", pageInfo);
//			res.put("orderList", orderList);
//			return new ResponseEntity<Map<String,Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	// 결제 완료(매칭, 주문) 상세 보기
//	@GetMapping("/orderdetail/{farmerId}/{ordersId}/{type}")
//	public ResponseEntity<OrdersDto> orderDetail(@PathVariable Long farmerId,
//			@PathVariable Long ordersId, @PathVariable String type) {
//		try {
//			OrdersDto dto = null;
//			if(type.equals("0")) { // 매칭
//				dto = farmerService.OrdersDetailQuotationId(farmerId, ordersId);
//			} else if(type.equals("1")){ // 주문
//				dto = farmerService.OrdersDetailNotQuotationId(farmerId, ordersId);
//			}
//			return new ResponseEntity<OrdersDto>(dto, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<OrdersDto>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// 발송 완료
//	
//	// 판매 취소
//	
//	// 배송 현황(배송중, 배송완료)
//	
//	// 정산 내역
//	
//}
