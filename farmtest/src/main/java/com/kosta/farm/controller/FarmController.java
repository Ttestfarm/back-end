package com.kosta.farm.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.OrderHistoryDto;
import com.kosta.farm.dto.PayInfoSummaryDto;
import com.kosta.farm.dto.QuotePayDto;
import com.kosta.farm.dto.RequestCopyDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.RequestWithQuotationCountDTO;
import com.kosta.farm.dto.ReviewDto;
import com.kosta.farm.dto.ReviewInfoDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.service.FarmService;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.PaymentStatus;

import lombok.extern.slf4j.Slf4j;
@Slf4j // 로그 선언
@RestController
public class FarmController {
	@Autowired
	private FarmService farmService;

	// 리뷰 작성하기
	@PostMapping("/buylist")
	public ResponseEntity<String> insertReview(@ModelAttribute ReviewDto review, MultipartFile reviewpixUrl,
			Authentication authentication) {
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
		String userName = user.getUserName();
		try {
			request.setUserId(userId);
			request.setName(userName);
			farmService.addRequest(request);
			return ResponseEntity.ok("요청서를 등록했습니다");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("요청서 등록을 실패했습니다: " + e.getMessage());
		}
	}

	@PatchMapping("/mypage/delete/{requestId}") // 요청서 삭제하기(상태변경)
	public ResponseEntity<String> deleteRequest(@PathVariable Long requestId) {
		try {
			// 요청서 상태를 cancel로 변경
			Request updatedRequest = farmService.updateRequestStateToCANCEL(requestId);
			return ResponseEntity.ok("요청서를 삭제했습니다");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/findfarmer/{farmerId}") // default 기본페이지 디테일
	public ResponseEntity<Map<String, Object>> farmerDetail(@PathVariable(name = "farmerId") Long farmerId,
			Authentication authentication) {
		try {
			Map<String, Object> res = new HashMap<>();
			Farmer farmer = farmService.farmerDetail(farmerId);
			res.put("farmer", farmer);
			if (authentication != null) {
				User user = (User) authentication.getPrincipal();
				Long userId = user.getUserId();
				Boolean farmerfollow = farmService.selectedFarmerfollow(userId, farmerId); // 이걸 모르겠음
				res.put("farmerfollow", farmerfollow); // true면 follow 눌림
			}
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 파머 follow 했는지 여부 분별 후 안되었으면 follow가 된다
	@GetMapping("/findfarmer/{farmerId}/follow")
	public ResponseEntity<Map<String, Object>> farmerFollow(Authentication authentication,
			@PathVariable Long farmerId) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			Boolean selectFarmer = farmService.farmerfollow(userId, farmerId);
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
			List<ReviewInfoDto> reviewList = farmService.getReviewListInfoByFarmer(farmerId, pageInfo);
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

	@GetMapping("farmer/productlist/{page}") // 등록한 상품 리스트 페이징
	public ResponseEntity<Map<String, Object>> getProductListByFarmer(Authentication authentication,
			@PathVariable(required = false) Integer page) {
		User user = (User) authentication.getPrincipal();
		Long farmerId = user.getFarmerId();
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

	@GetMapping("farmer/reviewlist/{page}") // 받은 리뷰 리스트 페이징
	public ResponseEntity<Map<String, Object>> getReviewListByFarmer(Authentication authentication,
			@PathVariable(required = false) Integer page) {
		User user = (User) authentication.getPrincipal();
		Long farmerId = user.getFarmerId();
		try {
			Map<String, Object> res = new HashMap<>();
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<ReviewInfoDto> reviewList = farmService.getReviewListInfoByFarmer(farmerId, pageInfo);
			res.put("reviewList", reviewList);
			res.put("pageInfo", pageInfo);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	// 유저의 파머찜리스트 무한스크롤
	@GetMapping("/user/followlist")
	public ResponseEntity<Map<String, Object>> getFollowingFarmersByUserId(Authentication authentication,
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page)

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
			log.info("res",res);
			return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/user") // 마이페이지 메인 받은 매칭 견적 list
	public ResponseEntity<Map<String, Object>> matchingList(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		long startTime = System.currentTimeMillis(); // 요청 처리 시작 시간 측정
		try {
			List<RequestWithQuotationCountDTO> requestWithCountList = farmService
					.getRequestsWithQuotationCountByUser(userId);
			Map<String, Object> res = new HashMap<>();
			res.put("requestWithCountList", requestWithCountList);
			long endTime = System.currentTimeMillis(); // 요청 처리 종료 시간 측정
			long duration = endTime - startTime; // 요청 처리 시간 계산
			// 처리 시간 로그 출력
			System.out.println("요청 처리 시간: " + duration + "밀리초"); //27밀리초
			log.info("요청 처리 시간: " + duration + "밀리초");
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	// 구매내역 불러오기 하기 후기도 같이 불러옴 무한스크롤 필터기능
	@GetMapping("/user/buylist")
	public ResponseEntity<Map<String, Object>> buyList(Authentication authentication,
			@RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
			@RequestParam(required = false) PaymentStatus state) {
		User user = (User) authentication.getPrincipal();
		Long userId = user.getUserId();
		try {
			Map<String, Object> res = new HashMap<>();
			PageInfo pageInfo = PageInfo.builder().curPage(page).build();
			List<PayInfoSummaryDto> buyList = state != null
					? farmService.findBuyListByUserAndState(pageInfo, userId, state)
					: farmService.findBuyListByUser(pageInfo, userId);
			List<OrderHistoryDto> OrdersWithReview = new ArrayList<>();
			List<Review> reviewList = farmService.getReviewListByUser(userId);
			System.out.println(reviewList);
			for (PayInfoSummaryDto payInfoSummaryDto : buyList) {
				String receiptId = payInfoSummaryDto.getReceiptId();
				OrderHistoryDto orderHistory = new OrderHistoryDto();
				orderHistory.setPayInfo(payInfoSummaryDto);
				// order에 따른 리뷰
				Review findreview = findReviewForOrder(reviewList, receiptId);
				if (findreview != null) {
					orderHistory.setReview(findreview);
				}

				OrdersWithReview.add(orderHistory);
			}
			res.put("OrdersWithReview", OrdersWithReview);
			res.put("pageInfo", pageInfo);
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

	@GetMapping("user/request/{quotationId}") // 받은 매칭 견적서에서 견적서 checkout
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

	// 요청서에서 따라사기 버튼 누르면 요청품목과 수량을 불러온다
	@GetMapping("/matching/buy/{requestId}") // matching/buy?reqformId=요청서번호
	public ResponseEntity<RequestCopyDto> copyRequest(Authentication authentication, @PathVariable Long requestId) {
		try {
			RequestCopyDto request = farmService.requestCopy(requestId);
			return ResponseEntity.ok().body(request); // json으로 변환해서 줌
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null); // 예외 처리

		}
	}

}