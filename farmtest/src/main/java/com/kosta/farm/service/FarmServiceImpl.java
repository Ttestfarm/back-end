package com.kosta.farm.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.ProductFile;
import com.kosta.farm.entity.Review;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FarmerfollowRepository;
import com.kosta.farm.repository.OrdersRepository;
import com.kosta.farm.repository.ProductFileRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.ReviewRepository;
import com.kosta.farm.util.PageInfo;
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
	private final OrdersRepository ordersRepository;

	@Override
	public void regFarmer(Farmer farmer) throws Exception {
		farmerRepository.save(farmer);

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

	@Override
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
	public Long farmerCount() throws Exception {
		return farmDslRepository.findFarmerCount();
	}

//	@Override // farmerfollow 유무 감별
//	public Boolean isFarmerfollow(Long userId, Long farmerId) throws Exception {
//		Farmer farmer = farmerRepository.findById(farmerId).get();
//		Farmerfollow farmerfollow = farmDslRepository.findFarmerfollow(userId, farmerId);
//		if (farmerfollow == null) {
//			farmerfollowRepository.save(Farmerfollow.builder().userId(userId).farmerId(farmerId).build());
//			farmer.setFollowCount(farmer.getFollowCount() + 1);
//			farmerRepository.save(farmer);
//			return true;
//		} else {
//			farmerfollowRepository.deleteById(farmerfollow.getFarmerId());
//			farmer.setFollowCount(farmer.getFollowCount() - 1);
//			farmerRepository.save(farmer);
//			return false;
//		}
//	}
//
//	@Override // farmerfollow 하기
//	public Boolean selFarmerfollow(Long userId, Long farmerId) throws Exception {
//		Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
//		if (cnt < 1)
//			return false;
//		return true;
//	}

	@Override
	public Farmer farmerInfo(Long farmerId) throws Exception {
		return null;
	}

	@Override
	public List<Farmerfollow> getFollowingFarmersByUserId(Long userId) throws Exception {
		return farmerfollowRepository.findByUserId(userId);
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

	@Override // 리뷰 쓰기
	public void insertReview(Review review) throws Exception {
		reviewRepository.save(review);
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

	@Override
	public Orders createOrder(Orders orders) throws Exception {
		return ordersRepository.save(orders);
	}

	@Override
	public void completePay(Long orderId) throws Exception {
		Orders orders = ordersRepository.findById(orderId).orElse(null);
		if (orders != null) {orders.getPaymentId();

		}

	}
}

//	@PostConstruct
//	public void initDB() {
//		List<Product> products=IntStream.rangeClosed(1,200).mapToObj(i ->new Product("product" + i, new Random().nextInt(100), new Random().nextInt(50000))).collect(Collectors.toList());
//		productRepository.saveAll(products);
//	}
//PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, category));
//List<Tuple> pages = farmDslRepository.getFarmersByCategory(pageInfo);
//return farmDslRepository.getFarmersByCategory(pageInfo);
//@Override
//public List<Farmer> farmerListByRating(PageInfo pageInfo) throws Exception {
//PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8, Sort.by(Sort.Direction.DESC, "rating"));
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

//@Override // 리뷰순으로 파머리스트
//public List<Farmer> farmerListByReview(PageInfo pageInfo) throws Exception {
//	PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 8,
//			Sort.by(Sort.Direction.DESC, "reviewCount"));
//	Page<Farmer> pages = farmerRepository.findAll(pageRequest);
//	pageInfo.setAllPage(pages.getTotalPages());
//	int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
//	int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
//
//	pageInfo.setStartPage(startPage);
//	pageInfo.setEndPage(endPage);
//	List<Farmer> farmerList = new ArrayList<>();
//	for (Farmer farmer : pages.getContent()) {
//		farmerList.add(farmer);
//	}
//	return farmerList;
//
//}
//PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10, Sort.by(Sort.Direction.DESC, "num"));
//Page<Board> pages = null;
//if (type.equals("subject")) {
//	pages = boardRepository.findBySubjectContains(keyword, pageRequest);
//
//} else if (type.equals("content")) {
//	pages = boardRepository.findByContentContains(keyword, pageRequest);
//
//} else if (type.equals("writer")) {
//	pages = boardRepository.findByMember_Id(keyword, pageRequest);
//
//} else {
//	return null;
//}
//// 특정 페이지의 모든걸 가져온다 수동으로 했던 거의 일부를 이게 해준다
//// 이게 allpage를 대체한다
//
//pageInfo.setAllPage(pages.getTotalPages());
//int startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
//int endPage = Math.min(startPage + 10 - 1, pageInfo.getAllPage());
//
//pageInfo.setStartPage(startPage);
//pageInfo.setEndPage(endPage);
//List<BoardDto> boardDtoList = new ArrayList<>();
//for (Board board : pages.getContent()) {
//	boardDtoList.add(board.toDto());
//
//}
//// 실제 조회한 데이터 목록? 쿼리와 관련된건 다 엔티티
//
//return boardDtoList;
//}
