package com.kosta.farm.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.ModifyFarmDto;
import com.kosta.farm.dto.PaymentDto;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.dto.RegFarmerDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.FileVo;
import com.kosta.farm.entity.PayInfo;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FileVoRepository;
import com.kosta.farm.repository.PayInfoRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.PaymentStatus;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

	private final UserService userService;

	// Repository
	private final FarmerRepository farmerRepository;
	private final QuotationRepository quotationRepository;
	private final FileVoRepository fileVoRepository;
	private final PayInfoRepository payInfoRepository;
	private final ProductRepository productRepository;
	// DSL
	private final FarmerDslRepository farmerDslRepository;
	private final ObjectMapper objectMapper;

	@Value("${upload.path}")
	private String dir;

	// ** 매칭 주문 요청서 보기 **
	// 파머 관신 농산물 조회
	@Override
	public List<String> findFarmInterestByFarmerId(Long farmerId) throws Exception {
		Farmer farmer = farmerRepository.findByFarmerId(farmerId);

		List<String> interestList = new ArrayList<String>();
		interestList.add(farmer.getFarmInterest1());
		interestList.add(farmer.getFarmInterest2());
		interestList.add(farmer.getFarmInterest3());
		interestList.add(farmer.getFarmInterest4());
		interestList.add(farmer.getFarmInterest5());

		return interestList;
	}

	// 관심 농산물인 요청서 리스트 보기
	@Override
	public List<Request> findRequestsByFarmInterestPageInfo(Long farmerId, String farmInterest, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 5); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<Request> list = farmerDslRepository.findRequestByInterestAndFarmerId(farmerId, farmInterest, pageRequest);
		
		if(pageInfo.getAllPage() == null ) {
			pageInfo.setAllPage((int) Math.ceil((double) farmerDslRepository.findRequestcountByInterestAndFarmerId(farmerId, farmInterest) / 5));				
		}
		return list;
	}
	
	// ** 견적서 **
	// 견적서 보내기(저장)
	@Override
	@Transactional
	public void saveQuotation(Quotation quotation, List<MultipartFile> images) throws Exception {
		String fileNums = "";
		System.out.println("before image save" + quotation.toString());
		if (images != null && images.size() != 0) {
			System.out.println("here");
			for (MultipartFile img : images) {
				System.out.println("directory : "+dir);
				// primgfiletable에 insert
				FileVo imageFile = FileVo.builder().directory(dir).fileName(img.getOriginalFilename())
						.size(img.getSize()).build();
				fileVoRepository.save(imageFile);

				// upload 폴더에 upload
				File uploadFile = new File(dir + imageFile.getFileId());
				img.transferTo(uploadFile);

				// file 번호 목록 만들기
				if (!fileNums.equals(""))
					fileNums += ",";
				fileNums += imageFile.getFileId();
			}
			quotation.setQuotationImages(fileNums);
		}
		// 견적서 DB에 저장
		System.out.println("before save "+quotation.toString());
		quotationRepository.save(quotation);
	}

	// ** 견적 현황 페이지 **
	// 견적서현황 state : CANCEL, READY, EXPIRED, COMPLETED
	@Override
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageInfo)
			throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		System.out.println("here1");
		List<Tuple> tuples = farmerDslRepository.findQuotationByFarmerIdAndStateAndPaging(farmerId, state, pageRequest);
		System.out.println(tuples.size());
		List<QuotationDto> quotList = new ArrayList<>();

		for (Tuple t : tuples) {
			QuotationDto dto = new QuotationDto();
			Quotation quot = t.get(0, Quotation.class);
			String address2 = t.get(1, String.class);

			dto.setQuotationId(quot.getQuotationId());
			dto.setQuotationProduct(quot.getQuotationProduct());
			dto.setQuotationQuantity(quot.getQuotationQuantity());
			dto.setQuotationPrice(quot.getQuotationPrice());

			dto.setAddress2(address2);
			dto.setNewState(quot.getState().name());

			quotList.add(dto);
			System.out.println(dto);
		}

		Long allCount = farmerDslRepository.findQuotationCountByFarmerId(farmerId, state);
		System.out.println(allCount);
		Integer allPage = (int) (Math.ceil(allCount.doubleValue() / pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		Integer endPage = Math.min(startPage + 10 - 1, allPage);

		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		return quotList;
	}

	// 견적서 취소
	@Override
	@Transactional
	public void updateQuotationByFarmerIdAndRequestIds(Long farmerId, List<Long> ids) throws Exception {
		farmerDslRepository.updateQuotationStateByfarmerIdAndRequestId(farmerId, ids);
	}

	// 견적서 자세히보기
	@Override
	public Quotation findQuotationByQuotationId(Long farmerId, Long quotationId) throws Exception {
		return farmerDslRepository.findQuotationByQuotationId(farmerId, quotationId);
	}

	// 파머 상품 등록
	public void productEnter(Product product, MultipartFile titleImage, List<MultipartFile> images) throws Exception {
		String fileNums = "";

		if (titleImage != null && !titleImage.isEmpty()) {
			FileVo imageFile = FileVo.builder().directory(dir).fileName(titleImage.getOriginalFilename())
					.size(titleImage.getSize()).build();
			fileVoRepository.save(imageFile);

			File uploadFile = new File(dir + imageFile.getFileId());
			titleImage.transferTo(uploadFile);
			product.setThumbNail(imageFile.getFileId());
		}

		if (images != null && images.size() != 0) {

			for (MultipartFile img : images) {
				// primgfiletable에 insert
				FileVo imageFile = FileVo.builder().directory(dir).fileName(img.getOriginalFilename())
						.size(img.getSize()).build();
				fileVoRepository.save(imageFile);

				// upload 폴더에 upload
				File uploadFile = new File(dir + imageFile.getFileId());
				img.transferTo(uploadFile);

				// file 번호 목록 만들기
				if (!fileNums.equals(""))
					fileNums += ",";
				fileNums += imageFile.getFileId();
			}
			product.setFileUrl(fileNums);
		}
		// product table에 insert
		productRepository.save(product);
	}

	// 결제 완료 현황
	@Override
	public List<PaymentDto> findOrdersByFarmerIdAndPage(Long farmerId, String type, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<PaymentDto> payList = new ArrayList<>();
		Long allCount = null;
		if (type.equals("matching")) { // 매칭 주문
			List<Tuple> tuples = farmerDslRepository.findOrdersQuotByFarmerIdAndPaging(farmerId, pageRequest);
			for (Tuple t : tuples) {
				PaymentDto dto = new PaymentDto();
				dto.setReceiptId(t.get(0, String.class));
				dto.setProductName(t.get(1, String.class));
				dto.setQuotationQuantity(t.get(2, String.class));
				dto.setProductPrice(t.get(3, Integer.class));
				dto.setBuyerName(t.get(4, String.class));
				dto.setBuyerTel(t.get(5, String.class));
				dto.setBuyerAddress(t.get(6, String.class) + t.get(7, String.class) + t.get(8, String.class));
				payList.add(dto);
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);

		} else if (type.equals("order")) { // 받은 주문
			List<PayInfo> tempList = farmerDslRepository.findOrdersByFarmerIdAndPaging(farmerId, pageRequest);
			for (PayInfo pay : tempList) {
				PaymentDto dto = new PaymentDto();
				dto.setReceiptId(pay.getReceiptId());
				dto.setProductName(pay.getProductName());
				dto.setQuotationQuantity(pay.getQuotationQuantity());
				dto.setProductPrice(pay.getProductPrice());
				dto.setBuyerName(pay.getBuyerName());
				dto.setBuyerTel(pay.getBuyerTel());
				dto.setBuyerAddress(pay.getBuyerAddress());
				payList.add(dto);
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);
		}

		Integer allPage = (int) (Math.ceil(allCount.doubleValue() / pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		Integer endPage = Math.min(startPage + 10 - 1, allPage);

		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		return payList;
	}

	// 결제 완료(매칭, 주문) 상세 보기
	public PaymentDto OrdersDetailQuotationId(Long farmerId, String receiptId, String type) throws Exception {
		PaymentDto payment = new PaymentDto();
		PayInfo p = null;
		System.out.println(type);
		if (type.equals("matching")) { // 매칭
			p = farmerDslRepository.findOrderByFarmerIdAndOrderIdIsNotNull(farmerId, receiptId);
		} else if (type.equals("order")) { // 주문
			p = farmerDslRepository.findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(farmerId, receiptId);
		}
			System.out.println(p.toString());
			payment.setReceiptId(p.getReceiptId());
			payment.setPgType(p.getPgType()); // 결제 방법
			payment.setPaymentDelivery(p.getPaymentDelivery()); // 배송비
			payment.setAmount(p.getAmount()); // 총 금액

			payment.setProductName(p.getProductName()); // 품목
			payment.setQuotationQuantity(p.getQuotationQuantity()); // 수량
			payment.setProductPrice(p.getProductPrice()); // 품목 가격

			payment.setBuyerName(p.getBuyerName());
			payment.setBuyerTel(p.getBuyerTel());
			payment.setBuyerAddress(p.getBuyerAddress());

//			Timestamp timestamp = p.getPaidAt();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String dateString = dateFormat.format(p.getPaidAt());
			payment.setPaidAt(dateString);

		return payment;
	}

	// 발송 완료 처리
	@Transactional
	public void insertDeliveryAndInvoice(Long farmerId, String receiptId, String tCode, String tName, String tInvoice)
			throws Exception {
		// System.out.println("2");
		// System.out.println("id " + ordersId);
		// System.err.println("tCode " + tCode);
		// System.out.println("tInvoice " + tInvoice);
		// payment 테이블에 배송 정보 저장
		PayInfo payment = payInfoRepository.findById(receiptId).get();
		payment.setTCode(tCode);
		payment.setTName(tName);
		payment.setTInvoice(tInvoice);

		// farmerDslRepository.updatePayment(receiptId, tCode, tName, tInvoice);

		LocalDate currentDate = LocalDate.now();

		int year = currentDate.getYear();
		int month = currentDate.getMonthValue();
		int day = currentDate.getDayOfMonth();

		// 20일 전이면 현재 월의 25일로 설정
		// 19일 이후면 다음 달의 25일로 설정
		if (day < 20) {
			currentDate = currentDate.withDayOfMonth(25);
		} else {
			currentDate = currentDate.plusMonths(1).withDayOfMonth(25);
		}

		String temp = currentDate + "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date utilDate = dateFormat.parse(temp);
		Date date = new Date(utilDate.getTime());

		// 정산 예정일
		payment.setInvoiceDate(date);
		BigDecimal commission = null;
		BigDecimal amount = payment.getAmount();
		if (payment.getQuotationId().equals(null)) {
			// matching
			payment.setInvoiceCommission(3);
			commission = new BigDecimal(0.03);
			
			BigDecimal money = amount.subtract(amount.multiply(commission));
			money = money.setScale(0, RoundingMode.HALF_UP);
			System.out.println(money.toString());
			payment.setInvoicePrice(money.toString()); // 정산금액 setter
		} else {
			// product
			payment.setInvoiceCommission(5);
			commission = new BigDecimal(0.05);
			
			BigDecimal money = amount.subtract(amount.multiply(commission));
			money = money.setScale(0, RoundingMode.HALF_UP);
			System.out.println(money.toString());
			payment.setInvoicePrice(money.toString()); // 정산금액 setter
		}

		// state 배송중(SHIPPING) 변경
		payment.setState(PaymentStatus.SHIPPING);

		// 변경 내용 저장
		payInfoRepository.save(payment);
	}

	// 판매 취소
	@Transactional
	public void deleteOrderState(Long farmerId, String receiptId, String cancelText) throws Exception {
		// payment state 변경 (CANCEL) 및 cancelText (취소 사유) 추가
		farmerDslRepository.updatePaymentStateCANCEL(farmerId, receiptId, cancelText);
	}

	// 배송 현황 리스트
	@Override
	public List<PaymentDto> findDeliberyByFarmerIdAndDeliveryState(Long farmerId, String state,
			PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<PaymentDto> deliveryList = new ArrayList<>();
		Long allCount = null;

		List<PayInfo> payList = farmerDslRepository.findOrdersIdAndDeliveryAndProductAndByDeliveryState(farmerId, state,
				pageRequest);
		for (PayInfo p : payList) {
			PaymentDto dto = new PaymentDto();

			dto.setReceiptId(p.getReceiptId());

			dto.setTCode(p.getTCode());
			dto.setTName(p.getTName());
			dto.setTInvoice(p.getTInvoice());

			dto.setBuyerName(p.getBuyerName());
			dto.setBuyerTel(p.getBuyerTel());
			dto.setBuyerAddress(p.getBuyerAddress());

			dto.setProductName(p.getProductName());
			dto.setQuotationQuantity(p.getQuotationQuantity());
			dto.setProductPrice(p.getProductPrice());

			dto.setState(p.getState());

			deliveryList.add(dto);
		}
		allCount = farmerDslRepository.findDeliveryCountByFarmerIdAndDeliveryState(farmerId, state);
		System.out.println("allCount " + allCount);

		Integer allPage = (int) (Math.ceil(allCount.doubleValue() / pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		Integer endPage = Math.min(startPage + 10 - 1, allPage);

		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		return deliveryList;
	}

	// 정산내역
	public List<PaymentDto> findInvoicesByFarmerIdAndDateAndPage(Long farmerId, String sDate, String eDate, String state,
			PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<PayInfo> payList = null;
		List<PaymentDto> invoiceList = new ArrayList<>();
		Long allCount = null;

		payList = farmerDslRepository.findOrdersIdAndDeliveryAndProductAndByDeliveryState(farmerId, sDate, eDate, state,
				pageRequest);
		for (PayInfo p : payList) {

		}
		allCount = farmerDslRepository.findDeliveryCountByFarmerIdAndDeliveryState(farmerId, state);

		Integer allPage = (int) (Math.ceil(allCount.doubleValue() / pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage() - 1) / 10 * 10 + 1;
		Integer endPage = Math.min(startPage + 10 - 1, allPage);

		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		return invoiceList;
	}

	// 파머등록
	@Override
	public Farmer registerFarmer(RegFarmerDto request, MultipartFile farmPixurl) throws Exception {

		Farmer farmer = Farmer.builder()
				.farmName(request.getFarmName())
				.farmTel(request.getFarmTel())
				.farmAddress(request.getFarmAddress())
				.farmAddressDetail(request.getFarmAddressDetail())
				.registrationNum(request.getRegistrationNum())
				.farmBank(request.getFarmBank())
				.farmAccountNum(request.getFarmAccountNum())
				.build();

		// 관심품목 입력받아서 # 기준으로 파싱하여 각각 저장
		String[] interests = request.getFarmInterest().replaceAll("^\\s*#*", "").split("#");
		int numInterests = Math.min(interests.length, 5); // 최대 5개로 제한

		farmer.setFarmInterest1(numInterests > 0 ? interests[0].trim() : null);
		farmer.setFarmInterest2(numInterests > 1 ? interests[1].trim() : null);
		farmer.setFarmInterest3(numInterests > 2 ? interests[2].trim() : null);
		farmer.setFarmInterest4(numInterests > 3 ? interests[3].trim() : null);
		farmer.setFarmInterest5(numInterests > 4 ? interests[4].trim() : null);

		Farmer savedFarmer = farmerRepository.save(farmer);

		// if(telSelected) {
		// userService.updateUserTel(loginUser, request.getFarmTel());
		// }

		if (farmPixurl != null && !farmPixurl.isEmpty()) {

			// 파일명 설정
			String fileName = "profile_image_" + savedFarmer.getFarmerId() + "."
					+ StringUtils.getFilenameExtension(farmPixurl.getOriginalFilename());

			// 파일 저장 경로 설정
			String filePath = Paths.get(dir, fileName).toString();

			// 파일 저장
			farmPixurl.transferTo(new File(filePath));

			savedFarmer.setFarmPixurl(filePath);
		}

		return savedFarmer;
	}

	@Override
	public Farmer modifyFarmer(ModifyFarmDto request, MultipartFile farmPixurl) throws Exception {
		Long farmerId = request.getFarmerId();
		// FarmerRepository를 사용하여 farmerId로 기존 Farmer 객체를 가져옴
		Farmer farmer = farmerRepository.findById(farmerId).orElse(null);

		// Farmer 객체의 필드를 업데이트
		farmer.setFarmName(request.getFarmName());
		farmer.setFarmTel(request.getFarmTel());
		farmer.setFarmAddress(request.getFarmAddress());
		farmer.setFarmAddressDetail(request.getFarmAddressDetail());
		farmer.setRegistrationNum(request.getRegistrationNum());
		farmer.setFarmBank(request.getFarmBank());
		farmer.setFarmAccountNum(request.getFarmAccountNum());

		// 관심품목 입력받아서 # 기준으로 파싱하여 각각 저장
		String[] interests = request.getFarmInterest().replaceAll("^\\s*#*", "").split("#");
		int numInterests = Math.min(interests.length, 5); // 최대 5개의 관심사로 제한

		farmer.setFarmInterest1(numInterests > 0 ? interests[0].trim() : null);
		farmer.setFarmInterest2(numInterests > 1 ? interests[1].trim() : null);
		farmer.setFarmInterest3(numInterests > 2 ? interests[2].trim() : null);
		farmer.setFarmInterest4(numInterests > 3 ? interests[3].trim() : null);
		farmer.setFarmInterest5(numInterests > 4 ? interests[4].trim() : null);

		if (farmPixurl != null && !farmPixurl.isEmpty()) {
			String dir = "C:/Users/USER/upload";

			// 파일명 설정
			String fileName = "profile_image_" + farmer.getFarmerId() + "."
					+ StringUtils.getFilenameExtension(farmPixurl.getOriginalFilename());

			// 파일 저장 경로 설정
			String filePath = Paths.get(dir, fileName).toString();
			System.out.println("filePath: " + filePath);
			// 파일 저장
			farmPixurl.transferTo(new File(filePath));

			farmer.setFarmPixurl(filePath);
		}
		Farmer modifiedFarmer = farmerRepository.save(farmer);
		return modifiedFarmer;
	}

	@Override
	public Farmer getFarmerById(Long farmerId) throws Exception {
		if (farmerId == null) {
			throw new IllegalArgumentException("farmerId가 없습니다.");
		}
		
		Farmer farmer = farmerRepository.findByFarmerId(farmerId);

		if (farmer == null) {
			throw new RuntimeException("등록된 파머가 없습니다.");
		}

		return farmer;
	}

}
