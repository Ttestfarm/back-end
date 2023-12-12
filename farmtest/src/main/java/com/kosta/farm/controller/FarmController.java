package com.kosta.farm.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.config.auth.PrincipalDetails;
import com.kosta.farm.dto.FarmerDto;
import com.kosta.farm.dto.RequestWithQuotationCountDTO;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.service.FarmService;
import com.kosta.farm.util.PageInfo;

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

	@PostMapping("/matching") // 요청서 작성하기
	public ResponseEntity<Integer> writeRequest(@ModelAttribute Request request) {
		try {
			Integer num = farmService.addRequest(request).intValue();
			return new ResponseEntity<Integer>(num, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
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
			System.out.println(keyword);
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<FarmerDto> farmerList = null;
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

	@GetMapping("/findfarmer/{farmerId}/{userId}") // default 기본페이지 디테일
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable(name = "farmerId") Long farmerId,
//			@PathVariable(name="userId") Long userId,
			Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Long userId = principalDetails.getUser().getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			Farmer farmer = farmService.farmerDetail(farmerId);
			res.put("farmer", farmer);
			Boolean farmerfollow = farmService.selectedFarmerfollow(userId, farmerId); // 이걸 모르겠음
			res.put("farmerfollow", farmerfollow); // true면 follow 눌림
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

	@GetMapping("/matching") // 매칭 메인 페이지 requestlist를 보여준다
	public ResponseEntity<Map<String, Object>> Matching(
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Request> matchingList = farmService.requestListByPage(pageInfo);
			Map<String, Object> res = new HashMap<>();
			res.put("matchingList", matchingList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
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

	// 유저의 파머찜리스트
	@GetMapping("/mypage/followlist")
	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Long userId = principalDetails.getUser().getUserId();
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

	// 파머찜 했는지 여부 안되었으면 파머찜이 된다
	@GetMapping("/findfarmer/{farmerId}/follow/{userId}")
	public ResponseEntity<Map<String, Object>> farmerFollow(Authentication authentication,
			@PathVariable Long farmerId) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Long userId = principalDetails.getUser().getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			Boolean selectFarmer = farmService.farmerfollow(userId, farmerId); // authentication 부터 아이디 가져옴
			res.put("isSelect", selectFarmer);
			Integer followCount = farmService.farmerInfo(farmerId).getFollowCount();
			res.put("followCount", followCount);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/mypage") // 받은 매칭 견적 list
	public ResponseEntity<Map<String, Object>> matchingList(
//			@RequestParam Long userId
			Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Long userId = principalDetails.getUser().getUserId();

		try {
			Map<String, Object> res = new HashMap<>();
			List<Request> requestList = farmService.requestListByUser(userId);
			List<RequestWithQuotationCountDTO> requestWithCountList = new ArrayList<>();
			for (Request request : requestList) {
				Long requestId = request.getRequestId(); // 각 요청의 ID 가져오기
				Long quoteCount = farmService.quoteCount(requestId); // 요청 ID에 대한 견적 수 가져오기
				RequestWithQuotationCountDTO requestWithCount = new RequestWithQuotationCountDTO();
				requestWithCount.setRequest(request);
				requestWithCount.setQuotationCount(quoteCount);
				System.out.println(quoteCount);
				requestWithCountList.add(requestWithCount);
			}
			res.put("requestWithCountList", requestWithCountList);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/mypage/{requestId}") // 받은 매칭 견적에서 견적서 자세히 보기
	public ResponseEntity<Map<String, Object>> matchingListDetail(@PathVariable Long requestId) {

		try {
			Map<String, Object> res = new HashMap<>();
			List<Quotation> quote = farmService.quoteListByRequest(requestId);
			res.put("quote", quote);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);

		}

	}

	// 구매내역 불러오기 하기 이거도 해야함 후기도 같이 불러와야함
	@GetMapping("/mypage/buylist/{userId}")
	public ResponseEntity<Map<String, Object>> buyList(@PathVariable Long userId) {
		try {
			Map<String, Object> res = new HashMap<>();
			List<Orders> buyList = farmService.getOrdersListByUser(userId);
			List<Review> reviewList = farmService.getReviewListByUser(userId);
			farmService.getOrdersandReviewByUser(userId);
			res.put("buyList", buyList);
			res.put("reviewList", reviewList);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();

		}

	}

//	@GetMapping("/mypage/buylist/{ordersId}") // 주문 상세
//	public ResponseEntity<String> ordersDetail(@PathVariable Long ordersId) {
//		try {
//			Map<String, Object> res = new HashMap<>();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//		return null;
//
//	}

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