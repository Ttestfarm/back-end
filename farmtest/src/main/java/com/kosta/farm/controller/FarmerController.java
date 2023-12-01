package com.kosta.farm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.service.FarmerService;
import com.kosta.farm.unti.PageInfo;

@RestController
public class FarmerController {
	@Autowired
	private FarmerService farmerService;
	
	@GetMapping("/farmer/requestlist/{farmerId}/{farmInterest}")
	public ResponseEntity<List<Request>> requestList(@PathVariable Integer farmerId,
			@PathVariable String farmInterest) {
		try {
			List<Request> reqList = farmerService.findRequestsByFarmInterest(farmInterest);
			return new ResponseEntity<List<Request>>(reqList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Request>>(HttpStatus.BAD_REQUEST);
		}
	}
	
//	@PostMapping("/farmer/regquot") 
//	public String regQuotation(@RequestBody Quotation quot) {
//		try {
//			
//			return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	@GetMapping("/farmer/quotstate/{page}/{farmerId}/{state}")
	public ResponseEntity<Map<String, Object>> quotList(@PathVariable Integer page, 
			@PathVariable Long farmerId, @PathVariable String state) {
		try {
			PageInfo pageInfo = new PageInfo(page);
			List<QuotationDto> quotList = farmerService.findQuotationByFarmerIdAndStateAndPage(farmerId, state, pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("pageInfo", pageInfo);
			res.put("quotList", quotList);
			return new ResponseEntity<Map<String,Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
		}
	}
}
