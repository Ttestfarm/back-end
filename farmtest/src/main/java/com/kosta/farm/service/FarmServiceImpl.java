package com.kosta.farm.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.OrderHistoryDto;
import com.kosta.farm.dto.ProductInfoDto;
import com.kosta.farm.dto.QuotationInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.dto.ReviewDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.FileVo;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
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

	@Override // 파머 서치리스트 sorting 추가
	public List<FarmerInfoDto> farmerSearchList(String keyword, String sortType, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, sortType));
		Page<Farmer> pages = farmerRepository

				.findByFarmInterest1ContainingOrFarmInterest2ContainingOrFarmInterest3ContainingOrFarmInterest4ContainingOrFarmInterest5Containing(
						keyword, keyword, keyword, keyword, keyword, pageRequest);

		pageInfo.setAllPage(pages.getTotalPages()); // 나머지가 필요 없다 b/c 무한 스크롤 현재 페이지랑 마지막 페이지만 필요하단
		List<FarmerInfoDto> farmerDtoList = new ArrayList<>();
		for (Farmer farmer : pages.getContent()) {
			farmerDtoList.add(farmer.toDto());
		}

		return farmerDtoList;
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

	@Override // 파머리스트 sorting으로
	public List<FarmerInfoDto> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 16,
				Sort.by(Sort.Direction.DESC, sortType).and(Sort.by(Sort.Direction.DESC, "farmerId")));
		Page<Farmer> pages = farmerRepository.findAll(pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		// 나머지가 필요 없다 b/c 무한 스크롤 현재 페이지랑 마지막 페이지만 필요하다
		List<FarmerInfoDto> farmerDtoList = new ArrayList<>();
		for (Farmer farmer : pages.getContent()) {
			farmerDtoList.add(farmer.toDto());
		}
		return farmerDtoList;
	}

	@Override
	public List<Farmerfollow> getFollowingFarmersByUserId(Long userId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 2,
				Sort.by(Sort.Direction.DESC, "farmerFollowId").and(Sort.by(Sort.Direction.DESC, "farmerId")));
		Page<Farmerfollow> pages = farmerfollowRepository.findByUserId(userId, pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		List<Farmerfollow> farmerfollowList = new ArrayList<>();
		for (Farmerfollow farmerfollow : pages.getContent()) {
			farmerfollowList.add(farmerfollow);
		}
		return farmerfollowList;
	}

//	@Override
//	public List<Farmerfollow> getFollowingFarmersByUserId(Long userId) throws Exception {
////		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 9,
////				Sort.by(Sort.Direction.DESC, "farmerfollowId").and(Sort.by(Sort.Direction.DESC, "farmerId")));
//		return farmerfollowRepository.findByUserId(userId);
//	}
	

	// 요청서 쓰기
	@Override
	public Request addRequest(RequestDto request) throws Exception {
		Request Nrequest = Request.builder().requestProduct(request.getRequestProduct())
				.requestDate(request.getRequestDate()).requestMessage(request.getRequestMessage())
				.requestQuantity(request.getRequestQuantity()).address1(request.getAddress1()).userId(request.getUserId())
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
		Optional<Review> oReview = reviewRepository.findByReceiptId(orders.getReceiptId());
		if (oReview.isPresent())
			throw new Exception("이미 존재하는 리뷰가 있습니다");

		Review review = Review.builder().receiptId(orders.getReceiptId()).rating(rating).content(content)
				.farmerId(orders.getFarmerId()).userId(orders.getUserId()).build();
		System.out.println(content);

		String fileNums = "";

		if (reviewpixUrl != null && !reviewpixUrl.isEmpty()) {

			String dir = "c:/jisu/upload/";

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
	public Long addReviews(ReviewDto review, List<MultipartFile> files) throws Exception {
		return null;
	}

	@Override
	public Long productEnter(Product product, MultipartFile thumbNail, List<MultipartFile> files) throws Exception {
		String dir = "c:/jisu/upload/";
		String fileNums = "";
		if (thumbNail != null && !thumbNail.isEmpty()) {
			FileVo imageFile = FileVo.builder().directory(dir).fileName(thumbNail.getOriginalFilename())
					.size(thumbNail.getSize()).build();
			productFileRepository.save(imageFile);
			File uploadFile = new File(dir + imageFile.getFileId());
			thumbNail.transferTo(uploadFile);
			product.setThumbNail(imageFile.getFileId());
		}

		if (files != null && files.size() != 0) {

			for (MultipartFile file : files) {
				// primgfiletable에 insert
				FileVo imageFile = FileVo.builder().directory(dir).fileName(file.getOriginalFilename())
						.size(file.getSize()).build();
				productFileRepository.save(imageFile);

				// upload 폴더에 upload
				File uploadFile = new File(dir + imageFile.getFileId());
				file.transferTo(uploadFile);

				// file 번호 목록 만들기
				if (!fileNums.equals(""))
					fileNums += ",";
//				fileNums += imageFile.getFileId();
			}
			product.setFileUrl(fileNums);
		}
		// product table에 insert
		productRepository.save(product);
		return product.getProductId();
	}

	@Override
	public void readImage(Integer num, ServletOutputStream outputStream) throws Exception {
		String dir = "c:/jisu/upload/";
		FileInputStream fis = new FileInputStream(dir + num);
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
//			String userName=userRepository.findUserNameByUserId(review.getUserId());

			reviewList.add(review);
		}
		return reviewList;
	}

	@Override // 파머별로 상품리스트 가져오기
	public List<Product> getProductListByFarmer(Long farmerId, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 3,
				Sort.by(Sort.Direction.DESC, "productId"));
		// 몇개씩 넣을지 고민
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

	@Override // 이미 했으면 지워진다
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

	@Override // 매칭 메인페이지
	@Transactional
	public List<RequestDto> requestListByPage(PageInfo pageInfo) throws Exception {

		return farmDslRepository.requestListWithNameByPage(pageInfo);
	}

//	PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 3,
//			Sort.by(Sort.Direction.DESC, "requestId"));
//	Page<Request> pages = requestRepository.findAll(pageRequest);
//	pageInfo.setAllPage(pages.getTotalPages());
//	List<Request> requestList = new ArrayList<>();
//	for (Request request : pages.getContent()) {
//		requestList.add(request);
//	}
//	return requestList;
	// 유저별로 리퀘스트쓴거
	@Override // 노 스크롤 노페이지네이션
	public List<Request> requestListByUser(Long userId) throws Exception {
		return requestRepository.findRequestByUserId(userId);
	}

	@Override // 유저별로 오더리스트 가져오기
	public List<PayInfo> getOrdersListByUser(Long userId) throws Exception {
		return payInfoRepository.findPayInfoByUserId(userId);
	}

	@Override // 견적서 리스트 가져오기 request에 맞는
	public List<Quotation> quoteListByRequest(Long requestId) throws Exception {
		return quoteRepository.findQuoteByRequestId(requestId);
	}

	@Override // order랑 review를 가져오기 이거 안됨 오늘 해야함
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
	public Double avgTotalRating() throws Exception {
		List<Farmer> farmers = farmerRepository.findAll();
		double totalRating = 0;
		Integer numberofFarmers = farmers.size();
		for (Farmer farmer : farmers) {
			totalRating += farmer.getRating();
		}
		return numberofFarmers > 0 ? totalRating / numberofFarmers : 0;

	}

	@Override
	public Long requestCountByState(RequestStatus state) throws Exception {
		List<Request> requests = requestRepository.findByState(state);
		return (long) requests.size();
	}

	@Override
	public Map<String, Object> quoteWithFarmerByRequestId(Long requestId) throws Exception {
		return farmDslRepository.findQuotationsWithFarmerByRequestId(requestId);
	}

	@Override
	public ProductInfoDto getProductInfoFromOrder(PayInfo payInfo) throws Exception {
		String receiptId = payInfo.getReceiptId();
		Long productId = payInfo.getProductId();
		Product product = productRepository.findById(productId).get();

		// Product로부터 필요한 정보를 가져와 ProductInfoDto에 설정
		if (product != null) {
			ProductInfoDto productInfo = new ProductInfoDto();
			productInfo.setProductName(product.getProductName());
			productInfo.setThumbNail(product.getThumbNail());

			// 다른 필요한 정보들 설정

			return productInfo;
		}
		return null; // 상품 정보가 없으면 null 반환
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

	@Override
	public OrderHistoryDto getOrderDetails(String recieptId) throws Exception {
		PayInfo payInfo= payInfoRepository.findById(recieptId).get();
		OrderHistoryDto orderHistoryDto = new OrderHistoryDto();
		orderHistoryDto.setPayInfo(payInfo);
		return null;
	}

	@Override
	public void savePaymentInfo(PayInfo payInfo) throws Exception {
		payInfoRepository.save(payInfo);
		Product product = null;
		if (payInfo.getQuotationId() == null) {
			product = productRepository.findById(payInfo.getProductId()).get();
			Integer currentStock = product.getProductStock();
			if (currentStock == (null) || (currentStock < payInfo.getCount())) {
				throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + currentStock);
			} else {
				product.setProductStock(currentStock-payInfo.getCount());
				productRepository.save(product);
			}
		} 
	}


}
