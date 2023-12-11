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
	@PostMapping("/farmer/regprod")
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

	@GetMapping("findfarmer") //파머찾기 검색, sorting기능
	public ResponseEntity<Map<String, Object>> findFarmer(
			@RequestParam(required = false, name = "keyword", defaultValue= "all") String keyword,
			@RequestParam(required = false, name= "sortType",defaultValue = "farmerId") String sortType,
			@RequestParam(required = false, name = "page", defaultValue="1") Integer page) {
		
		try {

			if (sortType == null || sortType.equals("") || sortType.equals("latest")) {
				sortType = "farmerId";
			}
			System.out.println(keyword);
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Farmer> farmerList = null;
			if (keyword.equals("all")) {
				// 전체파머스 리스트를 보여준다
				farmerList = farmService.findFarmersWithSorting(sortType, pageInfo);

			} else {
				farmerList = farmService.farmerSearchList(keyword, sortType, pageInfo);
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

	@GetMapping("/findfarmer/{farmerId}") // default 기본페이지 디테일
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable(name = "farmerId") Long farmerId) {
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

// 리뷰 리스트 페이징처리
	@GetMapping("findfarmer/{farmerId}/review/{page}")
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
	@GetMapping("findfarmer/{farmerId}/product/{page}")
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

	// 아직 다 안함
	@GetMapping("/matching") // 매칭 메인 페이지 requestlist를 보여준다
	public ResponseEntity<Map<String, Object>> Matching(@RequestParam(required = false) Integer page) {
		try {
//			List<Farmer> farmerList = farmService.findFarmersWithSorting(sortType, pageInfo);
//			Map<String, Object> res = new HashMap<>();
//			res.put("farmerList", farmerList);
//			res.put("pageInfo", pageInfo);
//			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);

			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

//	@PostMapping("/matching") //요청서 작성하기
//	public ResponseEntity<>

	// 구매내역 불러오기 하기 이거도 해야함 후기도 같이 불러와야함
	@GetMapping("/mypage/buylist/{userId}")
	public ResponseEntity<Map<String, Object>> buyList(@PathVariable Long userId) {
		try {
//			List<Orders> buyList = farmService.getOrdersListByUser(userId);
//			List<Review> reviewList= farmService.getReviewListByUser(userId);
			farmService.getOrdersandReviewByUser(userId);
			return new ResponseEntity<Map<String, Object>>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();

		}

	}

	@GetMapping("/mypage/buylist{ordersId}") // 주문 상세
	public ResponseEntity<String> ordersDetail(@PathVariable Long ordersId) {
		try {
			Map<String, Object> res = new HashMap<>();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;

	}

	// 리뷰 작성하기
	@PostMapping("/mypage/buylist/{ordersId}")
	public ResponseEntity<String> insertReview(@PathVariable Long ordersId, @RequestParam("rating") Integer rating,
			@RequestParam("content") String content, @RequestParam("files") List<MultipartFile> files) {
		try {
			farmService.addReview(ordersId, files, rating, content);
			return ResponseEntity.ok("리뷰 작성이 완료되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("리뷰 작성에 실패하였습니다");
		}

	}

	// 필요 없는 controller 이거 안씀
	@PostMapping("/placeorder")
	public ResponseEntity<String> placeOrders(@RequestParam Long productId, @RequestParam Long userId,
			@RequestParam Integer count, @RequestParam Long paymentId) {
		try {
			farmService.makeOrder(productId, paymentId);
			return new ResponseEntity<String>("주문이 성공적으로 생성되었습니다", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

	}

}
