package com.kosta.farm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.FarmerDto;
import com.kosta.farm.dto.OrderHistoryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.QuotationInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.RequestWithQuotationCountDTO;
import com.kosta.farm.dto.ReviewDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.service.FarmService;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.Tuple;

@RestController
public class FarmController {
	@Autowired
	private FarmService farmService;
	@Autowired
	private ProductRepository productRepository;

	// 리뷰 작성하기 완성? 다시 확인해보기
	@PostMapping("/buylist")
	public ResponseEntity<String> insertReview(
			@ModelAttribute ReviewDto review, MultipartFile reviewpixUrl) {
		try {
			farmService.addReview(review.getOrdersId(), reviewpixUrl, review.getRating(), review.getContent());
			return ResponseEntity.ok("리뷰 작성이 완료되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("리뷰작성 실패 " + e.getMessage());
		}

	}

	// 상품 등록 이거도 나머지 완성해야함
	@PostMapping("/farmer/regprod")
	public ResponseEntity<Integer> regProduct(@ModelAttribute Product product, MultipartFile mainFile,
			List<MultipartFile> additionalFiles) {
		try {
			Integer num = (farmService.productEnter(product, mainFile, additionalFiles)).intValue();
			System.out.println(num);
			return new ResponseEntity<Integer>(num, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
		}
	}

	// 거의 완성 이거 한번 붙여보기
	@PostMapping("/matching/{userId}") // 요청서 작성하기
	public ResponseEntity<String> writeRequest(
			@PathVariable Long userId,
			@ModelAttribute RequestDto request 
//			Authentication authentication
	) {
//		User user = (User) authentication.getPrincipal();
//		Long userId = user.getUserId();
		try {
//			String tel = user.getUserTel();
//			String address = user.getAddress1() + user.getAddress2() + user.getAddress3();
//			// 만약 저기서 adderss를 입력 안햇으면 불러와야함
			request.setUserId(userId);
			Request req = farmService.addRequest(request);
			return ResponseEntity.ok("요청서를 등록했습니다 : " + req);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("요청서 등록을 실패했습니다: " + e.getMessage());
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

//    User loginUser = userService.getLoginUserByUserEmail(auth.getName());
	@GetMapping("/findfarmer/{farmerId}") // default 기본페이지 디테일
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable(name = "farmerId") Long farmerId,
//			@PathVariable(name="userId") Long userId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			System.out.println("유저" + userId);
			Map<String, Object> res = new HashMap<>();
			Farmer farmer = farmService.farmerDetail(farmerId);
			res.put("farmer", farmer);
			// 왜 여기서 에러가 뜰까 이거 다시보기
			Boolean farmerfollow = farmService.selectedFarmerfollow(userId, farmerId); // 이걸 모르겠음
			res.put("farmerfollow", farmerfollow); // true면 follow 눌림
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 파머 follow 했는지 여부 안되었으면 follow가 된다
	@GetMapping("/findfarmer/{farmerId}/follow")
	public ResponseEntity<Map<String, Object>> farmerFollow(
//			Authentication authentication,
			@RequestParam Long userId, @PathVariable Long farmerId) {
//		User user = (User) authentication.getPrincipal();
//		Long userId = user.getUserId();
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

// 리뷰 리스트 페이징처리
	@GetMapping("findfarmer/{farmerId}/review/{page}")
	public ResponseEntity<Map<String, Object>> farmerReviewList(@PathVariable(required = false) Integer page,
			@PathVariable(required = false) Long farmerId) {
		try {
			Map<String, Object> res = new HashMap<>();
//			PageInfo pageInfo = PageInfo.builder().curPage(1).build();
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
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
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
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
	// 별점 평균도 보여주고 매칭중도 보여주고 매칭완료도 보여줘야함
	// 최신순으로 보여줘야함
	public ResponseEntity<Map<String, Object>> matching(
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<RequestDto> matchingList = farmService.requestListByPage(pageInfo);
			Double average = farmService.avgTotalRating();
			Long matchingProgress = farmService.requestCountByState("1");
			// 매칭중 requeststate1
			Long foundMatching = farmService.requestCountByState("2");
			// 매칭완료 requeststate2
			Map<String, Object> res = new HashMap<>();
			res.put("matchingList", matchingList);
			res.put("average",Math.round(average*100.0)/100.0);
			res.put("matchingProgress", matchingProgress);
			res.put("foundMatching", foundMatching);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 유저의 파머찜리스트
//	@GetMapping({"/user/followlist", "/user/followlist/{page}"})
	@GetMapping("/user/followlist")
	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(Authentication authentication,
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page
//			@PathVariable(required = false) Integer page
			
			)

//			,
	{
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Farmerfollow> followingFarmers = farmService.getFollowingFarmersByUserId(userId, pageInfo);
			List<FarmerDto> followingFarmersDetails = new ArrayList<>();
			for (Farmerfollow farmerfollow : followingFarmers) {
				List<FarmerDto> farmerDetail = farmService.findfarmerDetail(farmerfollow.getFarmerId());
				followingFarmersDetails.addAll(farmerDetail);
			}
			Map<String, Object> res = new HashMap<>();
			res.put("isFollow", true);
			res.put("followingFarmers", followingFarmersDetails);
			res.put("pageInfo", pageInfo);
			System.out.println(res);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/user") // 받은 매칭 견적 list
	public ResponseEntity<Map<String, Object>> matchingList(
//			@RequestParam Long userId
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
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

	// 구매내역 불러오기 하기 후기도 같이 불러옴
	@GetMapping("/buylist")
	public ResponseEntity<Map<String, Object>> buyList(@RequestParam Long userId
//			,
//			Authentication authentication
	) {
//		User user = (User) authentication.getPrincipal();
//		Long userId = user.getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			List<Orders> buyList = farmService.getOrdersListByUser(userId);
			List<OrderHistoryDto> OrdersWithReview = new ArrayList<>();
			List<Review> reviewList = farmService.getReviewListByUser(userId);
			for (Orders orders : buyList) {
				Long ordersId = orders.getOrdersId();
				OrderHistoryDto orderHistory = new OrderHistoryDto();
				orderHistory.setOrders(orders);
//			    Product productInfo = farmService.getProductInfoFromOrder(orders); // getProductInfo()를 통해 Product 정보 가져오기
//			    orderHistory.setProductInfo(productInfo); // OrderHistoryDto에 Product 정보 설정
				// order에 따른 리뷰
				Review findreview = findReviewForOrder(reviewList, ordersId);
				if (findreview != null) {
					orderHistory.setReview(findreview);
				}
				// 주문에 대한 상품 정보(ProductInfoDto) 가져오기
				ProductInfoDto productInfo = farmService.getProductInfoFromOrder(orders);
				if (productInfo != null) {
					orderHistory.setProductInfo(productInfo);
				}
				// 주문에 대한 견적 정보(QuotationInfoDto) 가져오기
				QuotationInfoDto quotationInfo = farmService.getQuotationInfoFromOrder(orders);
				if (quotationInfo != null) {
					orderHistory.setQuotationInfo(quotationInfo);
				}
				OrdersWithReview.add(orderHistory);
			}
			res.put("OrdersWithReview", OrdersWithReview);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();

		}
	}

	private Review findReviewForOrder(List<Review> reviewList, Long ordersId) {
		for (Review review : reviewList) {
			if (review.getOrdersId().equals(ordersId)) {
				return review;
			}
		}
		return null; // 리뷰가 없으면 null로 반환
	}

//	private Product findProductForOrder(List<Product> productLis)

	@GetMapping("/user/{requestId}") // 받은 매칭 견적에서 견적서 자세히 보기
	public ResponseEntity<Map<String, Object>> matchingListDetail(@PathVariable Long requestId) {
		try {
			Map<String, Object> res = farmService.quoteWithFarmerByRequestId(requestId);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);

		}

	}

	@GetMapping("/mypage/buylist/") // 주문 상세 farmerController에 있는지 보기
	public ResponseEntity<String> ordersDetail(@RequestParam(required = false, name = "ordersId") Long ordersId) {
		try {
			Map<String, Object> res = new HashMap<>();
//address 
//tel 입력한 값?
//수령인 getusername아니면 입력한 값 불러오기
//배송메모
//payment에서 불러오기?

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;

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

	// 이거 무시
	@GetMapping("/order/{productId}")
	public String order(@PathVariable(value = "productId") Long productId, Model model) {
		try {
			System.out.println("productId: " + productId);
			Optional<Product> product = productRepository.findById(productId);
			if (product == null) {
				System.out.println("상품을 찾을 수 없습니다");
				model.addAttribute("error_msg", "상품을 찾을 수 없습니다");
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;

	}

}