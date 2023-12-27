package com.kosta.farm.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.PayInfoSummaryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.QuotationInfoDto;
import com.kosta.farm.dto.QuotePayDto;
import com.kosta.farm.dto.RequestCopyDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.ReviewInfoDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.FileVo;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FarmerfollowRepository;
import com.kosta.farm.repository.FileVoRepository;
import com.kosta.farm.repository.PayInfoRepository;
import com.kosta.farm.repository.ProductFileRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.repository.ReviewRepository;
import com.kosta.farm.repository.UserRepository;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.PaymentStatus;
import com.kosta.farm.util.RequestStatus;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {
	private final FarmDslRepository farmDslRepository;
	private final FarmerDslRepository farmerDslRepository;
	private final FarmerRepository farmerRepository;
	private final FarmerfollowRepository farmerfollowRepository;
	private final ObjectMapper objectMapper;
	private final ProductRepository productRepository;
	private final ProductFileRepository productFileRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final RequestRepository requestRepository;
	private final QuotationRepository quoteRepository;
	private final UserService userService;
	private final FileVoRepository fileVoRepository;
	private final PayInfoRepository payInfoRepository;
	private final RefundService refundService;

	@Value("${imp_key}")
	private String impKey;

	@Value("${imp_secret}")
	private String impSecret;

	@Value("$(upload.path)")
	private String dir;

	@Override // 이건 페이지네이션을 지원
	public List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8,
				Sort.by(Sort.Direction.DESC, "farmerId"));
		Page<Farmer> pages = farmerRepository.findAll(pageRequest);

		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());

		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		List<Farmer> farmerList = new ArrayList<>();
		for (Farmer farmer : pages.getContent()) {
			farmerList.add(farmer);
		}
		return farmerList;
	}

	@Override
	public List<FarmerInfoDto> findfarmerDetail(Long farmerId) throws Exception {
		List<Farmer> detail = farmerRepository.findListByFarmerId(farmerId);
		List<FarmerInfoDto> farmerDtoList = new ArrayList<>();
		for (Farmer farmer : detail) {
			farmerDtoList.add(farmer.toDto());

		}
		return farmerDtoList;
	}

	@Override
	public List<Farmerfollow> getFollowingFarmersByUserId(Long userId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 9,
				Sort.by(Sort.Direction.DESC, "farmerFollowId").and(Sort.by(Sort.Direction.DESC, "farmerId")));
		Page<Farmerfollow> pages = farmerfollowRepository.findByUserId(userId, pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		List<Farmerfollow> farmerfollowList = new ArrayList<>();
		for (Farmerfollow farmerfollow : pages.getContent()) {
			farmerfollowList.add(farmerfollow);
		}
		return farmerfollowList;
	}

	// 요청서 쓰기
	@Override
	public Request addRequest(RequestDto request) throws Exception {
		Request Nrequest = Request.builder().requestProduct(request.getRequestProduct())
				.requestDate(request.getRequestDate()).requestMessage(request.getRequestMessage())
				.requestQuantity(request.getRequestQuantity()).address1(request.getAddress1())
				.address2(request.getAddress2()).address3(request.getAddress3()).userId(request.getUserId())
				.name(request.getName())
				.tel(request.getTel()).state(RequestStatus.REQUEST).build();
		Request add = requestRepository.save(Nrequest);
		return add;
	}

	@Override // 리뷰 작성하기 ordersId에 해당하면 리뷰를 쓸 수 있다 근데 하나의 주문에 하나의 review만 쓸 수 있다 리뷰가 추가되면 파머
				// rating field 업데이트
	public void addReview(String receiptId, MultipartFile reviewpixUrl, Integer rating, String content)
			throws Exception {
		Optional<PayInfo> oPay = payInfoRepository.findById(receiptId);
		if (oPay.isEmpty())
			throw new Exception("해당하는 주문이 없습니다");
		PayInfo orders = oPay.get();

		Long userId = orders.getUserId();
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			throw new Exception("사용자 정보를 찾을 수 없습니다");
		String userName = user.get().getUserName(); // username가져오기

		Optional<Review> oReview = reviewRepository.findByReceiptId(orders.getReceiptId());
		if (oReview.isPresent())
			throw new Exception("이미 존재하는 리뷰가 있습니다");

		Review review = Review.builder().receiptId(orders.getReceiptId()).rating(rating).content(content)
				.farmerId(orders.getFarmerId()).userId(orders.getUserId()).userName(userName).build();
		System.out.println(content);

		String fileNums = "";

		if (reviewpixUrl != null && !reviewpixUrl.isEmpty()) {

			FileVo imageFile = FileVo.builder().directory(dir).fileName(reviewpixUrl.getOriginalFilename())
					.size(reviewpixUrl.getSize()).build();
			fileVoRepository.save(imageFile);

			// upload 폴더에 upload
			File uploadFile = new File(dir + imageFile.getFileId());
			reviewpixUrl.transferTo(uploadFile);
			if (!fileNums.equals(""))
				fileNums += ",";
			fileNums += imageFile.getFileId();

		}
		review.setReviewpixUrl(fileNums);
		// 리뷰 작성하기
		reviewRepository.save(review);

		// 판매자의 리뷰 목록 가져오기
		List<Review> farmerReviews = reviewRepository.findAllByFarmerId(review.getFarmerId()); // farmersid에 해당하는 리뷰 목록

		// 해당 판매자의 평균 별점 업데이트
		Farmer farmer = farmerRepository.findById(review.getFarmerId())
				.orElseThrow(() -> new Exception("해당 판매자를 찾을 수 없습니다."));
		farmer.updateAvgRating(farmerReviews);
		Integer reviewCount = farmerReviews.size();
		farmer.setReviewCount(reviewCount);

		// 업데이트된 판매자 엔티티 저장
		farmerRepository.save(farmer);

	}

	@Override
	public void readImage(String url, ServletOutputStream outputStream) throws Exception {
		FileInputStream fis = new FileInputStream(dir + url);
		FileCopyUtils.copy(fis, outputStream);
		fis.close();
	}

//리뷰
	@Override // 리뷰숫자순으로 파머 가져오기
	public List<Farmer> getFarmerByReviewCount(Integer reviewCount) throws Exception {
		return farmerRepository.findByreviewCount(reviewCount);
	}

	@Override // 파머별로 리뷰리스트 가져오기 페이징처리도
	public List<Review> getReviewListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 3,
				Sort.by(Sort.Direction.DESC, "reviewId"));
		Page<Review> pages = reviewRepository.findByFarmerId(farmerId, pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		List<Review> reviewList = new ArrayList<>();
		for (Review review : pages.getContent()) {

			reviewList.add(review);
		}
		return reviewList;
	}

	@Override // 파머별로 상품리스트 가져오기
	public List<Product> getProductListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 3,
				Sort.by(Sort.Direction.DESC, "productId"));
		Page<Product> pages = productRepository.findProductByFarmerId(farmerId, pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		List<Product> productList = new ArrayList<>();
		for (Product product : pages.getContent()) {
			productList.add(product);
		}
		return productList;
	}

	@Override
	public List<ReviewInfoDto> getReviewListInfoByFarmer(Long farmerId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 6, Sort.by(Direction.DESC, "reviewId")); // 6개씩
		List<ReviewInfoDto> reviewList = farmDslRepository.reviewListWithFarmNameByPage(farmerId, pageRequest);
		Long allCount = farmDslRepository.reviewCountByFarmer(farmerId);
		Integer allPage = allCount.intValue() / pageRequest.getPageSize();

		if (allCount % pageRequest.getPageSize() != 0)
			allPage += 1;
		Integer startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		Integer endPage = Math.min(startPage + 10 - 1, allPage);
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		return reviewList;
	}

	@Override // 유저별로 리뷰리스트 가져오기
	public List<Review> getReviewListByUser(Long userId) throws Exception {
		return farmDslRepository.findByUserId(userId);
	}

	// 모든 농부 리스트 불러오기
	@Override
	public List<Farmer> findAllFarmers() {
		return farmerRepository.findAll();
	}

	@Override // farmerfollow하기
	public Boolean farmerfollow(Long userId, Long farmerId) throws Exception {
		Farmer farmer = farmerRepository.findById(farmerId).get();
		Farmerfollow farmerfollow = farmDslRepository.findFarmerfollow(userId, farmerId);
		if (farmerfollow == null) {
			farmerfollowRepository.save(Farmerfollow.builder().userId(userId).farmerId(farmerId).build());
			farmer.setFollowCount(farmer.getFollowCount() + 1);
			farmerRepository.save(farmer);
			return true;
		} else {
			farmerfollowRepository.deleteById(farmerfollow.getFarmerFollowId());
			farmer.setFollowCount(farmer.getFollowCount() - 1);
			farmerRepository.save(farmer);
			return false;
		}

	}

	@Override // 이미 팔로우 했으면 지워진다
	public Boolean selectedFarmerfollow(Long userId, Long farmerId) throws Exception {
		Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
		if (cnt < 1)
			return false;
		return true;
	}

	@Override // 파머 디테일페이지 가져오기
	public Farmer farmerDetail(Long farmerId) throws Exception {
		Optional<Farmer> ofarmer = farmerRepository.findById(farmerId);
		if (ofarmer.isEmpty())
			throw new Exception("해당 농부를 찾을 수 없습니다");
		return ofarmer.get();
	}

	@Override
	public Farmer farmerInfo(Long farmerId) throws Exception {
		return farmerRepository.findById(farmerId).get();
	}

	// 유저별로 리퀘스트쓴거
	@Override
	public List<Request> requestListByUser(Long userId) throws Exception {
		return requestRepository.findRequestByUserIdAndStateOrderByRequestIdDesc(userId, RequestStatus.REQUEST);
	}

	@Override // 유저별로 오더리스트 가져오기
	public List<PayInfo> getOrdersListByUser(Long userId) throws Exception {
		return payInfoRepository.findPayInfoByUserId(userId);
	}

	@Override // 견적서 리스트 가져오기 request에 맞는
	public List<Quotation> quoteListByRequest(Long requestId) throws Exception {
		return quoteRepository.findQuoteByRequestId(requestId);
	}

	@Override // order랑 review를 가져오기
	public List<PayInfo> getOrdersandReviewByUser(Long userId) throws Exception {
		return farmDslRepository.findPayInfowithReviewByUserId(userId);

	}

	@Override
	public List<Tuple> quoteandRequestListByRequestId(Long requestId) throws Exception {
		return farmDslRepository.getReqandQuoteByRequestId(requestId);
	}

	@Override
	public Long quoteCount(Long requestId) throws Exception {
		return farmDslRepository.findQuoteCountByRequestId(requestId);
	}

	@Override
	public Long farmerCount() throws Exception {
		return farmDslRepository.findFarmerCount();
	}

	@Override
	public Map<String, Object> quoteWithFarmerByRequestId(Long requestId) throws Exception {
		return farmDslRepository.findQuotationsWithFarmerByRequestId(requestId);
	}

	@Override
	public ProductInfoDto getProductInfoByProductId(Long productId) throws Exception {
		Optional<Product> oProduct = productRepository.findById(productId);
		if (oProduct.isPresent()) {
			Product product = oProduct.get();
			return null;
		}
		return null;
	}

	@Override
	public QuotationInfoDto getQuotationInfoFromOrder(PayInfo payInfo) throws Exception {
		Long quotationId = payInfo.getQuotationId();
		if (quotationId == null) {
			return null;
		}
		try {
			Optional<Quotation> oQuote = quoteRepository.findById(quotationId);
			if (oQuote.isPresent()) {
				Quotation quote = oQuote.get();
				QuotationInfoDto quoteInfo = new QuotationInfoDto();
				quoteInfo.setQuotationProduct(quote.getQuotationProduct());
				quoteInfo.setQuotationPicture(quote.getQuotationImages());
				return quoteInfo;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override // payment정보 저장하기
	public void savePaymentInfo(PayInfo payInfo) throws Exception {
		payInfoRepository.save(payInfo);
		Product product = null;
		if (payInfo.getQuotationId() == null) {
			product = productRepository.findById(payInfo.getProductId()).get();
			Integer currentStock = product.getProductStock();
			if (currentStock == (null) || (currentStock < payInfo.getCount())) {
				throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + currentStock);
			} else {
				product.setProductStock(currentStock - payInfo.getCount());
				productRepository.save(product);
			}
		}
	}

	@Override
	public QuotePayDto getQuoteWithRequestInfoById(Long quotationId) throws Exception {
		Quotation quote = quoteRepository.findById(quotationId).orElse(null);
		if (quote != null) {
			Long requestId = quote.getRequestId(); // 견적서가 참조하는 요청서id
			// 요청서 정보 조회
			Request request = requestRepository.findById(requestId).orElse(null);

			if (request != null) {
				// QuotePayDto에 견적서 정보와 요청서 정보를 담아 반환
				return new QuotePayDto(quote, request);
			}

		}
		return null; // 둘다 존재하지 않으면 null;
	}

	@Override
	public List<PayInfoSummaryDto> findBuyListByUserAndState(PageInfo pageInfo, Long userId, PaymentStatus state)
			throws Exception {
		List<PayInfoSummaryDto> buyList = farmDslRepository.getPartialOrdersListByUserByPage(pageInfo, userId);
		return buyList.stream().filter(payInfo -> payInfo.getState().equals(state)).collect(Collectors.toList());
	}

	@Override
	public List<PayInfoSummaryDto> findBuyListByUser(PageInfo pageInfo, Long userId) throws Exception {
		return farmDslRepository.getPartialOrdersListByUserByPage(pageInfo, userId);
	}

//	@Scheduled(fixedRate = 60000) // 1분(60초) 간격으로 실행
	@Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행하도록 스케줄링
	@Override
	public void updateRequestState() throws Exception {
		List<Request> requestList= requestRepository.findByState(RequestStatus.REQUEST);
		for(Request request : requestList) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date requestDate = formatter.parse(request.getRequestDate()); // 문자열을 Date로 변환
                // 현재 날짜 구하기
                Date currentDate = new Date();

                // 날짜를 비교하여 요청 날짜가 현재 날짜 이전인지 확인
                if (requestDate.before(currentDate)) {
                    // 요청 날짜가 현재 날짜 이전인 경우
                    // 처리할 작업 수행
                    request.setState(RequestStatus.EXPIRED); //상태를 EXPIRED로 변경
                    requestRepository.save(request); // 변경된 상태 저장
                } else {
                    // 요청 날짜가 현재 날짜 이후인 경우 다른 작업 수행
                }
            } catch (ParseException e) {
                // 날짜 형식 변환 실패 시 예외 처리
                e.printStackTrace();
                System.out.println("Failed to parse date: " + request.getRequestDate()); // 요청된 날짜 값을 출력

            }
        }
    }
    
	@Override
	public RequestCopyDto requestCopy(Long requestId) throws Exception {
		Optional<Request> oRequest = requestRepository.findById(requestId);
		if (oRequest.isEmpty()) {
			throw new Exception("해당하는 정보 없음");
		}
		return oRequest.get().toDto();
	}


}
