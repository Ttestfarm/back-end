package com.kosta.farm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.farm.dto.CompanyDto;
import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.service.PublicService;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.RequestStatus;

@RestController
public class PublicController {
	
	@Autowired
	private PublicService publicService;
	
	// img 불러오기
	@GetMapping("/img/{num}")
	public void imageView(@PathVariable Integer num, HttpServletResponse response) {
		try {
			publicService.readImage(num, response.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 택배사 정보 제공
	@GetMapping("/companylist")
	public ResponseEntity<List<CompanyDto>> companyList() {
		Long farmerId = (long) 1;
		try {
			List<CompanyDto> comList = publicService.requestCompanyList();
			return new ResponseEntity<List<CompanyDto>>(comList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<CompanyDto>>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/matching") // 매칭 메인 페이지를 보여준다 무한스크롤
	public ResponseEntity<Map<String, Object>> matching(
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<RequestDto> matchingList = publicService.requestListByPage(pageInfo);
			Double average = publicService.avgTotalRating();
			Long matchingProgress = publicService.requestCountByState(RequestStatus.REQUEST);
			// 매칭중 
			Long foundMatching = publicService.requestCountByState(RequestStatus.MATCHED);
			// 매칭완료
			Map<String, Object> res = new HashMap<>();
			res.put("matchingList", matchingList);
			res.put("average", Math.round(average * 100.0) / 100.0);
			res.put("matchingProgress", matchingProgress);
			res.put("foundMatching", foundMatching);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
      } catch (Exception e) {
         e.printStackTrace();
         return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
      }
   }
	
	   @GetMapping("/findfarmer") // 파머찾기 검색, sorting기능
	   public ResponseEntity<Map<String, Object>> findFarmer(
	         @RequestParam(required = false, name = "keyword", defaultValue = "all") String keyword,
	         @RequestParam(required = false, name = "sortType", defaultValue = "farmerId") String sortType,
	         @RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {

	      try {
	         if (sortType == null || sortType.equals("") || sortType.equals("latest")) {
	            sortType = "farmerId";
	         }
	         PageInfo pageInfo = PageInfo.builder().curPage(page).build();
	         List<FarmerInfoDto> farmerList = null;
	         if (keyword.equals("all")) {
	            // 전체파머스 리스트를 보여준다
	            farmerList = publicService.findFarmersWithSorting(sortType, pageInfo);
	         } else {
	            farmerList = publicService.farmerSearchList(keyword, sortType, pageInfo);
	         }
	         Map<String, Object> res = new HashMap<>();
	         res.put("farmerList", farmerList);
	         res.put("pageInfo", pageInfo);
	         return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
	      } catch (Exception e) {
	         e.printStackTrace();
	         return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
	      }

		}
	
	
}
