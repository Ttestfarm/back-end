package com.kosta.farm.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.service.FarmService;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.Tuple;

@RestController
public class FarmController {
	@Autowired
	private FarmService farmService;
	@Autowired
	private FarmerRepository farmerRepository;

	// 상품 등록
	@PostMapping("farmer/regprod")
	public ResponseEntity<Integer> regProduct(@ModelAttribute Product product, MultipartFile mainFile,
			List<MultipartFile> additionalFiles) {
		try {
			System.out.println(additionalFiles.size());
			Integer num = (farmService.productEnter(product, mainFile, additionalFiles)).intValue();
			System.out.println(num);
			return new ResponseEntity<Integer>(num, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
		}
	}

	// 유저의 파머찜리스트
	@GetMapping("/user/followlist")
	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(@PathVariable Long userId) {
		try {
			List<Farmerfollow> followingFarmers = farmService.getFollowingFarmersByUserId(userId);
			Map<String, Object> res = new HashMap<>();
			res.put("success", true);
			res.put("followingFarmers", followingFarmers);
			System.out.println(res);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 파머찜 했는지 여부 이게 왜 안될까???
	@GetMapping("/farmerfollow/{farmerId}")
	public ResponseEntity<Map<String, Object>> farmerFollow(Authentication authentication,
			@PathVariable Long farmerId) {
		try {
			Map<String, Object> res = new HashMap<>();
			Boolean selectFarmer = farmService.Farmerfollow(null, farmerId); // authentication 부터 아이디 가져옴
			res.put("isSelect", selectFarmer);
			Integer followCount = farmService.farmerInfo(farmerId).getFollowCount();
			res.put("followCount", followCount);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/findfarmer/{page}") // 파머찾기 메인페이지 정렬을 추가한
	public ResponseEntity<Map<String, Object>> findFarmer(@PathVariable(required = false) Integer page,
			@RequestParam(required = false, defaultValue = "farmerId") String sortType) {
		try {
			if (page == null) {
				page = 0;
			}
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Farmer> farmerList = farmService.findFarmersWithSorting(sortType, pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("farmerList", farmerList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/findfarmer/search/{page}/{keyword}") // 파머 검색 interest로 //한글 안깨지는인코딩
	public ResponseEntity<Map<String, Object>> searchFarmer(@PathVariable(required = false) Integer page,
			@PathVariable(required = false) String keyword) {
		try {
			System.out.println(keyword);
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Farmer> farmerList = farmService.FarmerSearchList(keyword, pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("farmerList", farmerList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

// 리뷰 리스트 페이징처리
	@GetMapping("findfarmer/detail/{farmerId}/review/{page}")
	public ResponseEntity<Map<String, Object>> farmerReviewList(@PathVariable(required = false) Integer page,
			@PathVariable(required = false) Long farmerId) {
		try {
			Map<String, Object> res = new HashMap<>();

			PageInfo pageInfo = PageInfo.builder().curPage(1).build();
			List<Review> reviewList = farmService.getReviewListByFarmer(farmerId, pageInfo);
			res.put("reviewList", reviewList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}

	}

	// 프로덕트 리스트 페이징처리
	@GetMapping("findfarmer/detail/{farmerId}/product/{page}")
	public ResponseEntity<Map<String, Object>> farmerProductList(@PathVariable(required = false) Integer page,
			@PathVariable(required = false) Long farmerId) {
		try {
			Map<String, Object> res = new HashMap<>();

			PageInfo pageInfo = PageInfo.builder().curPage(1).build();
			List<Product> productList = farmService.getProductListByFarmer(farmerId, pageInfo);
			res.put("productList", productList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("findfarmer/detail/{farmerId}") // default 기본페이지
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable Long farmerId) {
		try {
			Map<String, Object> res = new HashMap<>();
			Farmer farmer = farmService.farmerDetail(farmerId);
			res.put("farmer", farmer);
			PageInfo pageInfo1 = PageInfo.builder().curPage(1).build();
			List<Product> productList = farmService.getProductListByFarmer(farmerId, pageInfo1);
			res.put("productList", productList);
			// 리뷰리스트 불러오기
			PageInfo pageInfo2 = PageInfo.builder().curPage(1).build();
			List<Review> reviewList = farmService.getReviewListByFarmer(farmerId, pageInfo2);
			res.put("reviewList", reviewList);
//			Boolean farmerfollow = farmService.selectedFarmerfollow(null, farmerId); // 이걸 모르겠음
//			res.put("farmerfollow", farmerfollow); // true면 follow 눌림
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	
	//아직 다 안함
	@GetMapping("/matching") // 매칭 메인 페이지
	public ResponseEntity<Map<String, Object>> Matching(@RequestParam(required = false) Integer page) {
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
	}

	// 구매내역 불러오기
	@GetMapping("/user/buylist")
	public ResponseEntity<List<Orders>> buyList() {
		List<Orders> buyList = new ArrayList<>();
		return null;

	}

	// 오더 끝내기
	
	

//	
//	@GetMapping("/findfarmer/{page}") // 파머찾기 메인페이지 정렬을 추가한
//	public ResponseEntity<Map<String, Object>> findFarmer(@PathVariable(required = false) Integer page,
//			@RequestParam(required = false, defaultValue = "farmerId") String sortType) {
//		try {
//			if (page == null) {
//				page = 0;
//			}
//			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
//			List<Farmer> farmerList = farmService.findFarmersWithSorting(sortType, pageInfo);
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

//	@PostMapping("/matching") //요청서 작성하기

//	@PostMapping("/matching") //요청서 작성하기
//	@GetMapping("test/{id}")
//	@GetMapping("/findfarmer/{page}") // 파머찾기 메인페이지 이거 되는거
//	public ResponseEntity<Map<String, Object>> findFarmer(@PathVariable(required = false) Integer page) {
//		try {
//			if (page == null) {
//				page = 0;
//			}
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
//	@GetMapping("/findfarmer/{page}/{reviewcount}")
//	public ResponseEntity<Map<String, Object>> findFarmerbyReview(@PathVariable(required = false) Integer page) {
//		try {
//			PageInfo pageInfo= PageInfo.builder().curPage(page).build();
//			List<Farmer>farmerorderbyreview= farmService.farmerListByReview(pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("farmerorderbyreview", farmerorderbyreview);
//			res.put("pageInfo", pageInfo);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
//		}catch(Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
//		}
//	}
//	@GetMapping("/findfarmer/{page}") // 파머찾기 메인페이지 정렬 reviewcount랑 rating
//	public ResponseEntity<Map<String, Object>> findFarmer(@PathVariable(required = false) Integer page,
//			@RequestParam(required = false, defaultValue = "id") String sortBy) {
//		try {
//			if (page == null) {
//				page = 0;
//			}
//			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
//	        PageRequest pageRequest;
//	        if ("reviewcount".equals(sortBy)) {
//	            pageRequest = PageRequest.of(page, 10, Sort.by("reviewCount").descending());
//	        } else if ("rating".equals(sortBy)) {
//	            pageRequest = PageRequest.of(page, 10, Sort.by("rating").descending());
//	        } else {
//	            // 기본적으로 id 순으로 정렬
//	            pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());
//	        }
//			
//			
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

//	@GetMapping("/findfarmer") // 파머찾기 requestparam 가능ㅇㅇ
//	public ResponseEntity<List<Farmer>> findfarmermain(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
//		try {
//			Pageable pageable = PageRequest.of(page, size, Sort.by("farmerId").descending());
//			Page<Farmer> farmersList = farmerRepository.findAll(pageable);
//
//			return new ResponseEntity<List<Farmer>>(farmersList.getContent(), HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<List<Farmer>>(HttpStatus.BAD_REQUEST);
//		}
//	}
}
