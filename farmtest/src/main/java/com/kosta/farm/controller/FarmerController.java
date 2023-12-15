package com.kosta.farm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.CompanyDto;
import com.kosta.farm.dto.DeliveryDto;
import com.kosta.farm.dto.ErrorResponseDto;
import com.kosta.farm.dto.FarmerDto;
import com.kosta.farm.dto.InvoiceDto;
import com.kosta.farm.dto.OrdersDto;
import com.kosta.farm.dto.QuotDelDto;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.OrdersRepository;
import com.kosta.farm.service.APIService;
import com.kosta.farm.service.FarmerService;
import com.kosta.farm.unti.PageInfo;

@RestController
@RequestMapping("/farmer")
public class FarmerController {
	@Autowired
	private FarmerService farmerService;
	@Autowired
	private APIService apiService;

	// 매칭 주문 요청서 보기
	// farmerId를 받고 farmInterest return
	@GetMapping("/farmInterest")
	public ResponseEntity<Map<String, Object>> farmInterest(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
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
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
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
	public ResponseEntity<String> regQuotation(@RequestBody Quotation quot, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			quot.setFarmerId(farmerId);
			farmerService.saveQuotation(quot);
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
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			PageInfo pageInfo = new PageInfo(page);
			List<QuotationDto> quotList = farmerService.findQuotationByFarmerIdAndStateAndPage(farmerId, state, pageInfo);
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
	public ResponseEntity<String> quotdelete(@RequestBody QuotDelDto dto) {
		try {
			farmerService.updateQuotationByFarmerIdAndRequestIds(dto.getFarmerId(), dto.getIds());
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	// 견적서 상세보기
	@GetMapping("/quotdetail/{farmerId}/{quotationId}")
	public ResponseEntity<Quotation> quotdetail(@PathVariable Long farmerId, @PathVariable Long quotationId) {
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
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			PageInfo pageInfo = new PageInfo(page);
			List<OrdersDto> ordersList = farmerService.findOrdersByFarmerIdAndPage(farmerId, type, pageInfo);
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
	@GetMapping("/orderdetail/{ordersId}/{type}")
	public ResponseEntity<OrdersDto> orderDetail(Authentication authentication,
			@PathVariable Long ordersId, @PathVariable String type) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			System.out.println(type);
			OrdersDto orders = farmerService.OrdersDetailQuotationId(farmerId, ordersId, type);
			return new ResponseEntity<OrdersDto>(orders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<OrdersDto>(HttpStatus.BAD_REQUEST);
		}
	}

	// 택배사 정보 제공
	@GetMapping("companylist")
	public ResponseEntity<List<CompanyDto>> companyList(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			List<CompanyDto> comList = apiService.requestCompanyList();
			return new ResponseEntity<List<CompanyDto>>(comList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<CompanyDto>>(HttpStatus.BAD_REQUEST);
		}
	}
	
	// 발송 완료(delivery 생성, 택배사 코드, 운송장번호)
	@GetMapping("/sendparcel/{ordersId}/{code}/{name}/{invoice}")
	public ResponseEntity<String> delivery(Authentication authentication,
			@PathVariable Long ordersId, @PathVariable String code, @PathVariable String name,
			@PathVariable String invoice) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
//			System.out.println("1");
//			System.out.println("id " + ordersId);
//			System.err.println("code " + code);
//			System.out.println("invoice " + invoice);
			farmerService.insertDeliveryAndInvoice(farmerId, ordersId, code, name, invoice);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 판매 취소
	@PostMapping("/ordercancel")
	public ResponseEntity<String> delivery(Authentication authentication,
			@RequestBody OrdersDto ordDto) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			Long ordersId = ordDto.getOrdersId();
			String cancelText = ordDto.getCancelText();
			
			farmerService.deleteOrderState(farmerId, ordersId, cancelText);
			return new ResponseEntity<String>("성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("실패", HttpStatus.BAD_REQUEST);
		}
	}

	// 배송 현황(0: 오류, 1: 배송중, 2: 배송 완료)
	@GetMapping("/deliverylist/{state}/{page}")
	public ResponseEntity<Map<String, Object>> deliveryList(Authentication authentication, 
			@PathVariable Integer page, @PathVariable String state) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();
		try {
			if(page == 0) page = 1;
			PageInfo pageInfo = new PageInfo(page);
			List<DeliveryDto> deliveryList = farmerService.findDeliberyByFarmerIdAndDeliveryState(farmerId, state, pageInfo);
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
	@GetMapping("/invoice/{date}/{page}")
	public ResponseEntity<Map<String, Object>> InvoiceList(Authentication authentication,
			@PathVariable String date, @PathVariable Integer page) {
		User user = (User) authentication.getPrincipal();
		Long farmerId= user.getFarmerId();

		String[] dates = date.split("~");
		String sDate = dates[0];
		String eDate = dates[1];
		
		try {
			if(page == 0) page = 1;
			PageInfo pageInfo = new PageInfo(page);
			
			Map<String,Object> res = new HashMap<>();
			List<InvoiceDto> invoiceList =  farmerService.findInvoicesByFarmerIdAndDateAndPage(farmerId, date, pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}
}
