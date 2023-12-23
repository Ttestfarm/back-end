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

import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.OrderHistoryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.QuotationInfoDto;
import com.kosta.farm.dto.QuotePayDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.RequestWithQuotationCountDTO;
import com.kosta.farm.dto.ReviewDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.FarmService;
import com.kosta.farm.util.PageInfo;

@RestController
public class FarmController {
	@Autowired
	private FarmService farmService;

	// 리뷰 작성하기
	@PostMapping("/buylist")
	public ResponseEntity<String> insertReview(@ModelAttribute ReviewDto review, MultipartFile reviewpixUrl) {
		try {
			farmService.addReview(review.getReceiptId(), reviewpixUrl, review.getRating(), review.getContent());
			return ResponseEntity.ok("리뷰 작성이 완료되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("리뷰작성 실패 " + e.getMessage());
		}
	}

	@PostMapping("/matching/request") // 요청서 작성하기
	public ResponseEntity<String> writeRequest(@RequestBody RequestDto request, Authentication authentication) {
		System.out.println(request);
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			request.setUserId(userId);
			Request req = farmService.addRequest(request);
			return ResponseEntity.ok("요청서를 등록했습니다");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("요청서 등록을 실패했습니다: " + e.getMessage());
		}
	}

	@GetMapping("/findfarmer/{farmerId}") // default 기본페이지 디테일
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable(name = "farmerId") Long farmerId,
			Authentication authentication) {

		try {
			Map<String, Object> res = new HashMap<>();
			Farmer farmer = farmService.farmerDetail(farmerId);
			res.put("farmer", farmer);
			// 왜 여기서 에러가 뜰까 이거 다시보기
			if (authentication != null) {
				User user = (User) authentication.getPrincipal();
				Long userId = user.getUserId();
				System.out.println("유저" + userId);
				Boolean farmerfollow = farmService.selectedFarmerfollow(userId, farmerId); // 이걸 모르겠음
				res.put("farmerfollow", farmerfollow); // true면 follow 눌림
			}
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 파머 follow 했는지 여부 안되었으면 follow가 된다
	@GetMapping("/findfarmer/{farmerId}/follow")
	public ResponseEntity<Map<String, Object>> farmerFollow(Authentication authentication,
//         @RequestParam Long userId, 
			@PathVariable Long farmerId) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
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

	// 유저의 파머찜리스트
//	@GetMapping({"/user/followlist", "/user/followlist/{page}"})
	@GetMapping("/user/followlist")
	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(Authentication authentication,
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page
//			@PathVariable(required = false) Integer page

	)

	{
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<Farmerfollow> followingFarmers = farmService.getFollowingFarmersByUserId(userId, pageInfo);
			List<FarmerInfoDto> followingFarmersDetails = new ArrayList<>();
			for (Farmerfollow farmerfollow : followingFarmers) {
				List<FarmerInfoDto> farmerDetail = farmService.findfarmerDetail(farmerfollow.getFarmerId());
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

//
	@GetMapping("/user") // 마이페이지 메인 받은 매칭 견적 list
	public ResponseEntity<Map<String, Object>> matchingList(Authentication authentication) {
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
	public ResponseEntity<Map<String, Object>> buyList(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			List<PayInfo> buyList = farmService.getOrdersListByUser(userId);
			List<OrderHistoryDto> OrdersWithReview = new ArrayList<>();
			List<Review> reviewList = farmService.getReviewListByUser(userId);
			for (PayInfo payInfo : buyList) {
				String receiptId = payInfo.getReceiptId();
				OrderHistoryDto orderHistory = new OrderHistoryDto();
				orderHistory.setPayInfo(payInfo);
				// order에 따른 리뷰
				Review findreview = findReviewForOrder(reviewList, receiptId);
				if (findreview != null) {
					orderHistory.setReview(findreview);
				}
				// 주문에 대한 상품 정보(ProductInfoDto) 가져오기
				ProductInfoDto productInfo = farmService.getProductInfoFromOrder(payInfo);
				if (productInfo != null) {
					orderHistory.setProductInfo(productInfo);
				}
				// 주문에 대한 견적 정보(QuotationInfoDto) 가져오기
				QuotationInfoDto quotationInfo = farmService.getQuotationInfoFromOrder(payInfo);
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

	private Review findReviewForOrder(List<Review> reviewList, String receiptId) {
		for (Review review : reviewList) {
			if (review.getReceiptId().equals(receiptId)) {
				return review;
			}
		}
		return null; // 리뷰가 없으면 null로 반환
	}

	@GetMapping("/user/{requestId}") // 받은 매칭 견적에서 견적서리스트 자세히 보기
	public ResponseEntity<Map<String, Object>> matchingListDetail(@PathVariable Long requestId) {
		try {
			Map<String, Object> res = farmService.quoteWithFarmerByRequestId(requestId);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);

		}

	}

	@GetMapping("user/request/{quotationId}") // 받은 매칭 견적서에서 견적서 checkout..?
	public ResponseEntity<Map<String, Object>> quoteDetail(Authentication authentication,
			@PathVariable Long quotationId) {
		try {

			Map<String, Object> res = new HashMap<>();
			QuotePayDto quote = farmService.getQuoteWithRequestInfoById(quotationId);
			res.put("quote", quote);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

}