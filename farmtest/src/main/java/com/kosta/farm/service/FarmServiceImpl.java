package com.kosta.farm.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.entity.Delivery;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.ProductFile;
import com.kosta.farm.entity.Request;
import com.kosta.farm.entity.Review;
import com.kosta.farm.entity.User;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FarmerfollowRepository;
import com.kosta.farm.repository.OrdersRepository;
import com.kosta.farm.repository.PaymentRepository;
import com.kosta.farm.repository.ProductFileRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.repository.ReviewRepository;
import com.kosta.farm.repository.UserRepository;
import com.kosta.farm.util.PageInfo;

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
	private final OrdersRepository ordersRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final RequestRepository requestRepository;


	@Override
	public void regFarmer(Farmer farmer) throws Exception {
		// TODO Auto-generated method stub

	}

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

	@Override // 파머 서치리스트
	public List<Farmer> FarmerSearchList(String keyword, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8,
				Sort.by(Sort.Direction.DESC, "farmerId"));
		Page<Farmer> pages = farmerRepository
				.findByFarmInterest1ContainingOrFarmInterest2ContainingOrFarmInterest3Containing(keyword, keyword,
						keyword, pageRequest);
		pageInfo.setAllPage(pages.getTotalPages()); // 나머지가 필요 없다 b/c 무한 스크롤 현재 페이지랑 마지막 페이지만 필요하단
		List<Farmer> farmerList = new ArrayList<>();
		for (Farmer farmer : pages.getContent()) {
			farmerList.add(farmer);
		}

		return farmerList;
	}

	@Override //파머리스트 sorting으로
	public List<Farmer> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, sortType));
		Page<Farmer> pages = farmerRepository.findAll(pageRequest);
		pageInfo.setAllPage(pages.getTotalPages()); // 나머지가 필요 없다 b/c 무한 스크롤 현재 페이지랑 마지막 페이지만 필요하단
//		int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
//		int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
//		pageInfo.setStartPage(startPage);
//		pageInfo.setEndPage(endPage);
		List<Farmer> farmerList = new ArrayList<>();
		for (Farmer farmer : pages.getContent()) {
			farmerList.add(farmer);
		}
		return farmerList;
	}

	@Override
	public List<Farmerfollow> getFollowingFarmersByUserId(Long userId) throws Exception {
		return farmerfollowRepository.findByUserId(userId);
	}

	@Override // 리뷰 쓰기
	public void insertReview(Review review) throws Exception {
		reviewRepository.save(review);
	}

	@Override
	public Long productEnter(Product product, MultipartFile thumbNail, List<MultipartFile> files) throws Exception {
		String dir = "c:/jisu/upload/";
		String fileNums = "";
		if (thumbNail != null && !thumbNail.isEmpty()) {
			ProductFile imageFile = ProductFile.builder().directory(dir).fileName(thumbNail.getOriginalFilename())
					.size(thumbNail.getSize()).build();
			productFileRepository.save(imageFile);
			File uploadFile = new File(dir + imageFile.getProductFileId());
			thumbNail.transferTo(uploadFile);
			product.setThumbNail(imageFile.getProductFileId());
		}

		if (files != null && files.size() != 0) {

			for (MultipartFile file : files) {
				// primgfiletable에 insert
				ProductFile imageFile = ProductFile.builder().directory(dir).fileName(file.getOriginalFilename())
						.size(file.getSize()).build();
				productFileRepository.save(imageFile);

				// upload 폴더에 upload
				File uploadFile = new File(dir + imageFile.getProductFileId());
				file.transferTo(uploadFile);

				// file 번호 목록 만들기
				if (!fileNums.equals(""))
					fileNums += ",";
				fileNums += imageFile.getProductFileId();
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
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 6,
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
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 6,
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

	@Override //유저별로 오더리스트 가져오기
	public List<Orders> getOrdersListByUser(Long userId) throws Exception {
		return null;
//				ordersRepository.findAllById(userId);
	}

	// 모든 농부 리스트 불러오기
	@Override
	public List<Farmer> findAllFarmers() {
		return farmerRepository.findAll();
	}

	@Override // 파머 디테일페이지 가져오기
	public Farmer farmerDetail(Long farmerId) throws Exception {
		Optional<Farmer> ofarmer = farmerRepository.findById(farmerId);
		if (ofarmer.isEmpty())
			throw new Exception("해당 파머를 찾을 수 없습니다");
		return ofarmer.get();
	}

	@Override
	public Boolean Farmerfollow(Long userId, Long farmerId) throws Exception {
		Farmer farmer = farmerRepository.findById(farmerId).get();
		Farmerfollow farmerfollow = farmDslRepository.findFarmerfollow(userId, farmerId);
		if (farmerfollow == null) {
			farmerfollowRepository.save(Farmerfollow.builder().userId(userId).farmerId(farmerId).build());
			farmer.setFollowCount(farmer.getFollowCount() + 1);
			farmerRepository.save(farmer);
			return true;
		} else {
			farmerfollowRepository.deleteById(farmerfollow.getFarmerId());
			farmer.setFollowCount(farmer.getFollowCount() - 1);
			farmerRepository.save(farmer);
			return false;
		}
	}

	@Override // 데이터 조회
	public Boolean selectedFarmerfollow(Long userId, Long farmerId) throws Exception {
		Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
		if (cnt < 1)
			return false;
		return true;
	}

	@Override // payment상태 불러오기
	public Boolean checkPaymentState(Long userId) throws Exception {
		Payment payment = paymentRepository.findByUserId(userId);
		if ("결제완료".equals(payment.getState())) {
			return true;
		}
		return false;
	}

//	payment != null && 
	@Override
	public void makeOrder(Long productId, Long paymentId) throws Exception {
		// count 추가하기
		// 1. productId 로 product 조회
		// 상품의 현재 재고 확인
		// 결제상태 확인
		Optional<Payment> oPayment = paymentRepository.findById(paymentId);
		if (oPayment.isEmpty())
			throw new Exception("결제내역이 존재하지 않습니다");
		//
		Payment payment = oPayment.get();

		if (!payment.getState().equals("결제완료")) {
			throw new Exception("결제가 완료되지 않았습니다. 주문을 진행할 수 없습니다");
		}

//		// 2 product정보 일부내용을 builder를 이용하여 orders객체생성
		// 주문하려는 수량과 상품의 재고를 비교하여 확인
		Product product = productRepository.findById(productId).get();
		Integer currentStock = product.getProductStock();
		if (currentStock == (null) || (currentStock < payment.getCount())) {
			throw new Exception("상품의 재고가 부족합니다. 현재 재고 수량: " + currentStock);
		}
		// 상품 총가격 및 배송비 조회 임시 데이터(나중에 payment에서 가져와야함)

		Orders orders = Orders.builder().userId(payment.getUserId()).productId(productId)
				.farmerId(payment.getFarmerId()) // 파머 아이디도 저장
				.price(payment.getProductPrice()) // 총 가격은 상품 =가격 * 수량
				.ordersState(payment.getState()).paymentId(paymentId).count(payment.getCount()).build();
		try {
			// 3. repository 이용해서 save;
			ordersRepository.save(orders);
			product.removeStock(payment.getCount());
			productRepository.save(product);
		} catch (Exception e) {
			throw new Exception("주문을 처리하는 도중 오류가 발생했습니다");
		}
	}

	@Override
	public Long farmerCount() throws Exception {
		return null;
	}

	@Override
	public Farmer farmerInfo(Long farmerId) throws Exception {
		return null;
	}

	@Override //리퀘스트 메인페이지
	public List<Request> requestListByPage(PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, "requestId"));
		Page<Request> pages= requestRepository.findAll(pageRequest);
		pageInfo.setAllPage(pages.getTotalPages());
		List<Request> requestList= new ArrayList<>();
		for(Request request : pages.getContent()) {
			requestList.add(request);
		}
		return requestList;
	}
//	public List<Farmer> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception {
//		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, sortType));
//		Page<Farmer> pages = farmerRepository.findAll(pageRequest);
//		pageInfo.setAllPage(pages.getTotalPages()); // 나머지가 필요 없다 b/c 무한 스크롤 현재 페이지랑 마지막 페이지만 필요하단
////		int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
////		int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
////		pageInfo.setStartPage(startPage);
////		pageInfo.setEndPage(endPage);
//		List<Farmer> farmerList = new ArrayList<>();
//		for (Farmer farmer : pages.getContent()) {
//			farmerList.add(farmer);
//		}
//		return farmerList;
//	}
}

//@Override
//public List<Farmer> farmerListByfollower(PageInfo pageInfo) throws Exception {
//PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8,
//		Sort.by(Sort.Direction.DESC, "followCount"));
//Page<Farmer> pages = farmerRepository.findAll(pageRequest);
//pageInfo.setAllPage(pages.getTotalPages());
//int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
//int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
//pageInfo.setStartPage(startPage);
//pageInfo.setEndPage(endPage);
//List<Farmer> farmerList = new ArrayList<>();
//for (Farmer farmer : pages.getContent()) {
//	farmerList.add(farmer);
//}
//return farmerList;
//
//}

//@Override
//public void regFarmer(Farmer farmer) throws Exception {
//	farmerRepository.save(farmer);
//
//}
//

//
//@Override
//public Long farmerCount() throws Exception {
//	return farmDslRepository.findFarmerCount();
//}
//
//@Override
//public Boolean selectFarmerfollow(Integer userId, Integer farmerId) throws Exception {
//	Farmer farmer = farmerRepository.findById(farmerId).get();
//	Farmerfollow farmerfollow = farmDslRepository.findFarmerfollow(userId, farmerId);
//	if (farmerfollow == null) {
//		farmerfollowRepository.save(Farmerfollow.builder().userId(userId).farmerId(farmerId).build());
//		farmer.setFollowCount(farmer.getFollowCount() + 1);
//		farmerRepository.save(farmer);
//		return true;
//	} else {
//		farmerfollowRepository.deleteById(farmerfollow.getFarmerId());
//		farmer.setFollowCount(farmer.getFollowCount() - 1);
//		farmerRepository.save(farmer);
//		return false;
//	}
//}
//@Override
//public Boolean selectedFarmerfollow(Integer userId, Integer farmerId) throws Exception {
//	Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
//	if (cnt < 1)
//		return false;
//	return true;
//}
//
//@Override
//public Farmer farmerInfo(Integer farmerId) throws Exception {
//	return null;
//}
//
//@Override
//public List<Farmerfollow> getFollowingFarmersByUserId(Integer userId) throws Exception {
//	return farmerfollowRepository.findByUserId(userId);
//}
//
//@Override
//public Integer productEnter(Product product, MultipartFile thumbNail, List<MultipartFile> files) throws Exception {
//	String dir = "c:/jisu/upload/";
//	String fileNums = "";
//	if (thumbNail != null && !thumbNail.isEmpty()) {
//		Productfile imageFile = Productfile.builder().directory(dir).fileName(thumbNail.getOriginalFilename())
//				.size(thumbNail.getSize()).build();
//		productFileRepository.save(imageFile);
//		File uploadFile = new File(dir + imageFile.getFileId());
//		thumbNail.transferTo(uploadFile);
//		product.setThumbNail(imageFile.getFileId());
//	}
//
//	if (files != null && files.size() != 0) {
//
//		for (MultipartFile file : files) {
//			// primgfiletable에 insert
//			Productfile imageFile = Productfile.builder().directory(dir).fileName(file.getOriginalFilename())
//					.size(file.getSize()).build();
//			productFileRepository.save(imageFile);
//
//			// upload 폴더에 upload
//			File uploadFile = new File(dir + imageFile.getFileId());
//			file.transferTo(uploadFile);
//
//			// file 번호 목록 만들기
//			if (!fileNums.equals(""))
//				fileNums += ",";
//			fileNums += imageFile.getFileId();
//		}
//		product.setFileUrl(fileNums);
//	}
//	// product table에 insert
//	productRepository.save(product);
//	return product.getProductId();
//}
//
//@Override
//public void readImage(Integer num, ServletOutputStream outputStream) throws Exception {
//	String dir = "c:/jisu/upload/";
//	FileInputStream fis = new FileInputStream(dir + num);
//	FileCopyUtils.copy(fis, outputStream);
//	fis.close();
//}
//
////리뷰
//@Override // 리뷰숫자순으로 파머 가져오기
//public List<Farmer> getFarmerByReviewCount(Integer reviewCount) throws Exception {
//	return farmerRepository.findByreviewCount(reviewCount);
//}
//
//@Override
//public List<Review> getReviewListByFarmer(Integer farmerId) throws Exception {
//	return farmDslRepository.findByFarmerId(farmerId);
//}
//
//@Override
//public void insertReview(Review review) throws Exception {
//	reviewRepository.save(review);
//}
//
//@Override
//public List<Review> getReviewListByUser(Integer userId) throws Exception {
//	return farmDslRepository.findByUserId(userId);
//}

//@Override // farmerfollow 유무 감별
//public Boolean isFarmerfollow(Long userId, Long farmerId) throws Exception {
//	Farmer farmer = farmerRepository.findById(farmerId).get();
//	Farmerfollow farmerfollow = farmDslRepository.findFarmerfollow(userId, farmerId);
//	if (farmerfollow == null) {
//		farmerfollowRepository.save(Farmerfollow.builder().userId(userId).farmerId(farmerId).build());
//		farmer.setFollowCount(farmer.getFollowCount() + 1);
//		farmerRepository.save(farmer);
//		return true;
//	} else {
//		farmerfollowRepository.deleteById(farmerfollow.getFarmerId());
//		farmer.setFollowCount(farmer.getFollowCount() - 1);
//		farmerRepository.save(farmer);
//		return false;
//	}
//}
//
//@Override // farmerfollow 하기
//public Boolean selFarmerfollow(Long userId, Long farmerId) throws Exception {
//	Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
//	if (cnt < 1)
//		return false;
//	return true;
//}