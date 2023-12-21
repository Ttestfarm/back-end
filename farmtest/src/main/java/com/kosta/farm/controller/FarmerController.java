package com.kosta.farm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.PaymentDto;
import com.kosta.farm.dto.QuotDelDto;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.FarmerService;
import com.kosta.farm.util.PageInfo;

@RestController
@RequestMapping("/farmer")
public class FarmerController {
	
	@Autowired
	private FarmerService farmerService;
	
	// 매칭 주문 요청서 보기
	// farmerId를 받고 farmInterest return
	@GetMapping("/farmInterest")
	public ResponseEntity<Map<String, Object>> farmInterest(Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			Map<String, Object> res = new HashMap<>();
			List<String> interestList = farmerService.findFarmInterestByFarmerId(farmerId);
			List<Request> reqList = farmerService.findRequestsByFarmInterest(farmerId, interestList.get(0));
			res.put("interestList", interestList);
			res.put("reqList", reqList);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 관심 농산물인 요청서 리스트 보기
	@GetMapping("/requestlist")
	public ResponseEntity<List<Request>> requestList(@RequestParam String farmInterest, Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			List<Request> reqList = farmerService.findRequestsByFarmInterest(farmerId, farmInterest);
			return new ResponseEntity<List<Request>>(reqList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Request>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 견적서 보내기
	@PostMapping("/regquot")
	public ResponseEntity<String> regQuotation(@ModelAttribute Quotation quot, List<MultipartFile> images, Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			// 견적서 DB에 저장
			farmerService.saveQuotation(quot, images);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 파머 상품 등록
	@PostMapping("regproduct")
	public ResponseEntity<String> regProduct(@ModelAttribute Product product, MultipartFile titleImage, List<MultipartFile> images) {
		try {
			farmerService.productEnter(product, titleImage, images);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}
	
	
	// 견적 현황 페이지
	// 견적서 상태로(0 : 견적서 취소, 1 : 대기중, 2 : 기간 만료, 3 : 결제완료) 견적서 리스트 보여주기
	@GetMapping("/quotlist/{state}/{page}")
	public ResponseEntity<Map<String, Object>> quotList(@PathVariable String state, 
			@PathVariable Integer page, Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			PageInfo pageInfo = new PageInfo(page);
			List<QuotationDto> quotList = farmerService.findQuotationByFarmerIdAndStateAndPage(farmerId, state, pageInfo);
//			System.out.println(quotList.get(0).getAddress2());
			Map<String, Object> res = new HashMap<>();
			res.put("pageInfo", pageInfo);
			res.put("quotList", quotList);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 견적서 취소
	@PostMapping("/quotdelete")
	public ResponseEntity<String> quotdelete(@RequestBody QuotDelDto dto, Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		dto.setFarmerId(farmerId);
		try {
			farmerService.updateQuotationByFarmerIdAndRequestIds(dto.getFarmerId(), dto.getIds());
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 견적서 상세보기
	@GetMapping("/quotdetail/{quotationId}")
	public ResponseEntity<Quotation> quotdetail(@PathVariable Long quotationId,
			Authentication authentication) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			Quotation quot = farmerService.findQuotationByQuotationId(farmerId, quotationId);
			System.out.println(quot);
			return new ResponseEntity<Quotation>(quot, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Quotation>(HttpStatus.BAD_REQUEST);
		}
	}

	// 결제 완료 페이지
	@GetMapping("/orderlist/{type}/{page}")
	public ResponseEntity<Map<String, Object>> orderList(Authentication authentication,
			@PathVariable String type, @PathVariable Integer page) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			PageInfo pageInfo = new PageInfo(page);
			List<PaymentDto> ordersList = farmerService.findOrdersByFarmerIdAndPage(farmerId, type, pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("pageInfo", pageInfo);
			res.put("ordersList", ordersList);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 결제 완료(매칭, 주문) 상세 보기
	@GetMapping("/orderdetail/{receiptId}/{type}")
	public ResponseEntity<PaymentDto> orderDetail(Authentication authentication,
			@PathVariable String receiptId, @PathVariable String type) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			System.out.println(type);
			PaymentDto orders = farmerService.OrdersDetailQuotationId(farmerId, receiptId, type);
			return new ResponseEntity<PaymentDto>(orders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<PaymentDto>(HttpStatus.BAD_REQUEST);
		}
	}
	
	// 발송 완료(delivery 생성, 택배사 코드, 운송장번호)
	@GetMapping("/sendparcel/{receiptId}/{code}/{name}/{invoice}")
	public ResponseEntity<String> delivery(Authentication authentication,
			@PathVariable String receiptId, @PathVariable String code, @PathVariable String name,
			@PathVariable String invoice) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
//			System.out.println("1");
//			System.out.println("id " + ordersId);
//			System.err.println("code " + code);
//			System.out.println("invoice " + invoice);
			farmerService.insertDeliveryAndInvoice(farmerId, receiptId, code, name, invoice);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 판매 취소
	@PostMapping("/ordercancel")
	public ResponseEntity<String> delivery(Authentication authentication,
			@RequestBody PaymentDto payDto) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			String receiptId = payDto.getReceiptId();
			String cancelText = payDto.getCancelText();
			
			farmerService.deleteOrderState(farmerId, receiptId, cancelText);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 배송 현황( SHIPPING(배송중), COMPLETED(배송완료) )
	@GetMapping("/deliverylist/{state}/{page}")
	public ResponseEntity<Map<String, Object>> deliveryList(Authentication authentication, 
			@PathVariable Integer page, @PathVariable String state) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		try {
			// 처음 요청 시 page = 1로 설정
			if(page == 0) page = 1; 
			PageInfo pageInfo = new PageInfo(page);
			
			List<PaymentDto> deliveryList = farmerService.findDeliberyByFarmerIdAndDeliveryState(farmerId, state, pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("pageInfo", pageInfo);
			res.put("deliveryList", deliveryList);
			
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}
	
	// 정산 내역
	@GetMapping("/invoice/{date}/{state}/{page}")
	public ResponseEntity<Map<String, Object>> InvoiceList(Authentication authentication,
			@PathVariable String date, @PathVariable String state, @PathVariable Integer page) {
//		User user = (User) authentication.getPrincipal();
//		Long farmerId= user.getFarmerId();
		Long farmerId = (long) 1;
		// Payment의 invoiceDate(정산예정일)을 구하기 위해 날짜 구분
		String[] dates = date.split("~");
		String sDate = dates[0];
		String eDate = dates[1];
		
		try {
			// 처음 요청 시 page = 1로 설정
			if(page == 0) page = 1; 
			PageInfo pageInfo = new PageInfo(page);
			
			List<PaymentDto> invoiceList = farmerService.findInvoicesByFarmerIdAndDateAndPage(farmerId, sDate, eDate, state, pageInfo);

			Map<String,Object> res = new HashMap<>();
			res.put("pageInfo", pageInfo);
			res.put("invoiceList", invoiceList);

			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}
}
