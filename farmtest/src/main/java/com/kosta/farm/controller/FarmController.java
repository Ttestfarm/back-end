//package com.kosta.farm.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.kosta.farm.entity.Farmer;
//import com.kosta.farm.entity.Farmerfollow;
//import com.kosta.farm.entity.Product;
//import com.kosta.farm.entity.User;
//import com.kosta.farm.service.FarmService;
//import com.kosta.farm.util.PageInfo;
//
//@RestController
//public class FarmController {
//	@Autowired
//	private FarmService farmService;
//
//	// 상품 등록
//	@PostMapping("/regprod")
//	public ResponseEntity<Integer> regProduct(@ModelAttribute Product product, MultipartFile mainFile,
//			List<MultipartFile> additionalFiles) {
//		System.out.println(additionalFiles.size());
//		try {
//			Integer num = farmService.productEnter(product, mainFile, additionalFiles);
//			System.out.println(num);
//			return new ResponseEntity<Integer>(num, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// 파머찜리스트
//	@GetMapping("/followlist/{userId}")
//	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(@PathVariable Integer userId) {
//		try {
//			List<Farmerfollow> followingFarmers = farmService.getFollowingFarmersByUserId(userId);
//			Map<String, Object> res = new HashMap<>();
//			res.put("success", true);
//			res.put("followingFarmers", followingFarmers);
//			System.out.println(res);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// 파머찜 했는지 여부
//	@GetMapping("/farmerfollow/{farmerId}")
//	public ResponseEntity<Map<String, Object>> farmerFollow(@PathVariable Integer farmerId) {
//		try {
//			Map<String, Object> res = new HashMap<>();
//			Boolean selectFarmer = farmService.selectFarmerfollow(farmerId, farmerId);
//			res.put("isSelect", selectFarmer);
//			Integer followCount = farmService.farmerInfo(farmerId).getFollowCount();
//			res.put("followCount", followCount);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	@GetMapping("/findfarmer/{page}") // findfarmer main page
//	public ResponseEntity<Map<String, Object>> findFarmer(@PathVariable(required = false) Integer page) {
//		try {
//			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
//			List<Farmer> farmerList = farmService.farmerListByPage(pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("farmerList", farmerList);
//			res.put("pageInfo", pageInfo);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@GetMapping("/findfarmer/{prod}") // 파머 검색
//	public ResponseEntity<Map<String, Object>> searchFarmer(@PathVariable(required = false) Integer page,
//			@PathVariable(required = false) String category) {
//		try {
//			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
//			List<Farmer> farmerList = farmService.searchListByPage(category, pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("farmerList", farmerList);
//			res.put("pageInfo", pageInfo);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
////	@PostMapping("/matching") //요청서 작성하기
////	public Resp
//
//}
