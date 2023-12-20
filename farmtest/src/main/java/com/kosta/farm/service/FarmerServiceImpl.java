package com.kosta.farm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.PaymentDto;
import com.kosta.farm.dto.QuotationDto;
//import com.kosta.farm.entity.Delivery;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Payment;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
//import com.kosta.farm.repository.DeliveryRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.InvoiceRepository;
import com.kosta.farm.repository.PaymentRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.util.PageInfo;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

	// Repository
	private final FarmerRepository farmerRepository;
	private final PaymentRepository paymentRepository;
	private final RequestRepository requestRepository;
	private final QuotationRepository quotationRepository;
	private final InvoiceRepository invoiceRepository;
	// DSL
	private final FarmerDslRepository farmerDslRepository;
	private final ObjectMapper objectMapper;
	
	// ** 매칭 주문 요청서 보기 **
	// 파머 관신 농산물 조회
	@Override
	public List<String> findFarmInterestByFarmerId(Long farmerId) throws Exception{
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
	public List<Request> findRequestsByFarmInterest(Long farmerId, String farmInterest) throws Exception {
		List<Request> list = farmerDslRepository.findRequestByInterestAndFarmerId(farmerId, farmInterest);
		return list;
	}

	// ** 견적서 **
	@Override // 견적서 양식 (보내기 이벤트)-> 견적서 저장 
	public void saveQuotation(Quotation quotation) throws Exception {
		quotationRepository.save(quotation);
	}

	// ** 견적 현황 페이지 **
	// 견적서현황 state : CANCEL, READY, EXPIRED, COMPLETED 
	@Override 
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		System.out.println("here1");
		List<Tuple> tuples = farmerDslRepository.findQuotationByFarmerIdAndStateAndPaging(farmerId, state, pageRequest);
		System.out.println(tuples.size());
		List<QuotationDto> quotList = new ArrayList<>();
		
		for(Tuple t : tuples) {
			QuotationDto dto = new QuotationDto();
			Quotation quot = t.get(0, Quotation.class);
			String address2 = t.get(1, String.class);

			dto.setQuotationId(quot.getQuotationId());
			dto.setProduct(quot.getQuotationProduct());
			dto.setQuantity(quot.getQuotationQuantity());
			dto.setPrice(quot.getQuotationPrice());
			dto.setAddress2(address2);
			dto.setNewState(quot.getState().name());
			
			quotList.add(dto);
			System.out.println(dto);
		}
		
		Long allCount = farmerDslRepository.findQuotationCountByFarmerId(farmerId, state);
		System.out.println(allCount);
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
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

	// 결제 완료 현황
	@Override
	public List<PaymentDto> findOrdersByFarmerIdAndPage(Long farmerId, String type, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<PaymentDto> payList = new ArrayList<>();
		Long allCount = null;
		if(type.equals("1")) { // 매칭 주문
			List<Tuple> tuples = farmerDslRepository.findOrdersQuotByFarmerIdAndPaging(farmerId, pageRequest);
			for(Tuple t : tuples) {
				PaymentDto dto = new PaymentDto();
				dto.setReceiptId(t.get(0, String.class));
				dto.setProduct(t.get(1, String.class));
				dto.setCount(t.get(2, Integer.class));
				dto.setPrice(t.get(3, Integer.class));
				dto.setBuyerName(t.get(4, String.class));
				dto.setBuyerTel(t.get(5, String.class));
				dto.setBuyerAddress(t.get(6, String.class) + t.get(7, String.class) + t.get(8, String.class));
				payList.add(dto);
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);
			
		} else if(type.equals("2")) { // 받은 주문
			List<Payment> tempList = farmerDslRepository.findOrdersByFarmerIdAndPaging(farmerId, pageRequest);
			for(Payment pay : tempList) {
				PaymentDto dto = new PaymentDto();
				dto.setReceiptId(pay.getReceiptId());
				dto.setProduct(pay.getProduct());
				dto.setCount(pay.getCount());
				dto.setPrice(pay.getPrice());
				dto.setBuyerName(pay.getBuyerName());
				dto.setBuyerTel(pay.getBuyerTel());
				dto.setBuyerAddress(pay.getBuyerAddress());
				payList.add(dto);
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);
		}
		
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return payList;
	}
	
//	// 결제 완료(매칭) 상세 보기
//	public PaymentDto OrdersDetailQuotationId(Long farmerId, String receiptId, String type) throws Exception{
//		Tuple tuple = null;
//		PaymentDto payment = new PaymentDto();
//		if(type.equals("1")) {
//			tuple = farmerDslRepository.findOrderByFarmerIdAndOrderIdIsNotNull(farmerId, receiptId);
//		} else if(type.equals("2")) {
//			Payment temp = farmerDslRepository.findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(farmerId, receiptId);
//			payment.setReceiptId(temp.getReceiptId());
//
//		}
//		payment.setProduct(tuple.get(1, String.class));
//		payment.setQuantity(tuple.get(2, String.class));
//		payment.setName(tuple.get(3, String.class));
//		payment.setTel(tuple.get(4, String.class));
//		payment.setAddress(tuple.get(5, String.class));
//		payment.setPrice(tuple.get(6, Integer.class));
//		payment.setPaymentBank(tuple.get(7, String.class));
//		payment.setDelivery(tuple.get(8, Integer.class));
//		payment.setPaymentState(tuple.get(9, String.class));
//		payment.setPaymentPrice(tuple.get(10, Integer.class));
//		
//		Timestamp timestamp = tuple.get(11, Timestamp.class);
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String dateString = dateFormat.format(timestamp);        
//        payment.setDate(dateString);
//		
//		return orders;
//	}

//	// 발송 완료 처리
//	@Transactional
//	public void insertDeliveryAndInvoice(Long farmerId, Long ordersId, String tCode, String tName, String tInvoice) throws Exception {
////		System.out.println("2");
////		System.out.println("id " + ordersId);
////		System.err.println("tCode " + tCode);
////		System.out.println("tInvoice " + tInvoice);
//		deliveryRepository.save(Delivery.builder()
//				.ordersId(ordersId)
//				.tCode(tCode)
//				.tName(tName)
//				.tInvoice(tInvoice)
//				.build()
//				);
//		
//		LocalDate currentDate = LocalDate.now();
//
//        int year = currentDate.getYear();
//        int month = currentDate.getMonthValue();
//        int day = currentDate.getDayOfMonth();
//
//        // 20일 전이면 현재 월의 25일로 설정
//        // 19일 이후면 다음 달의 25일로 설정
//         if (day < 20) {
//            currentDate = currentDate.withDayOfMonth(25);
//        } else {
//            currentDate = currentDate.plusMonths(1).withDayOfMonth(25);
//        }
//        String temp = currentDate+"";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = (Date) dateFormat.parse(temp);
//        
////        invoiceRepository.save(Invoice.builder()
////        		 .farmerId(farmerId)
////        		 .orderId(ordersId)
////        		 .invoiceDate(date)
////        		 .invoiceCommission(3) // 매칭 3, 주문 5 구분
////        		 .invoicePrice(null) // payment id
////        		 );
////		farmerDslRepository.insertDeliveryWithOrdersIdAndTCodeAndTInvoice(ordersId, tCode, tInvoice);
//	}
//	
//	// 판매 취소
//	@Transactional
//	public void deleteOrderState(Long farmerId, Long ordersId, String cancelText) throws Exception {
//		// Orders State 변경
//		farmerDslRepository.deleteOrderState(ordersId, farmerId, cancelText);
//		
//		// Payment State 변경
//		Orders ord = ordersRepository.findById(ordersId).get();
//		Long paymentId = ord.getPaymentId();
//		farmerDslRepository.updatePaymentByOrdersId(paymentId);
//	}
//
//	// 배송 현황 리스트
////	@Override
////	public List<DeliveryDto> findDeliberyByFarmerIdAndDeliveryState(Long farmerId, String deliveryState,
////			PageInfo pageInfo) throws Exception {
////		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
////		List<Tuple> tuples = null;
////		List<DeliveryDto> deliveryList = new ArrayList<>();
////		Long allCount = null;
////		
////		tuples = farmerDslRepository.findOrdersIdAndDeliveryAndProductAndByDeliveryState(farmerId, deliveryState, pageRequest);
////		for(Tuple t : tuples) {
////				DeliveryDto dto = new DeliveryDto();
////				Long deliveryId = t.get(0, Long.class);
////				System.out.println(deliveryState);
////				Long ordersId = t.get(1, Long.class);
////				String tCode = t.get(2, String.class);
////				String tName = t.get(3, String.class);
////				String tInvoice = t.get(4, String.class);
////				String state = t.get(5, String.class);
////				String product = t.get(6, String.class);
////				String quantity = t.get(7, String.class);
////				Integer price = t.get(8, Integer.class);
////				String address = t.get(9, String.class);
////				
////				dto.setDeliveryId(deliveryId);
////				dto.setOrdersId(ordersId);
////				dto.setTCode(tCode);
////				dto.setTName(tName);
////				dto.setTInvoice(tInvoice);
////				dto.setDeliveryState(state);
////				dto.setProduct(product);
////				dto.setQuantity(quantity);
////				dto.setPrice(price);
////				dto.setAddress(address);
////				System.out.println(dto);
////				deliveryList.add(dto);
////		}
////		allCount = farmerDslRepository.findDeliveryCountByFarmerIdAndDeliveryState(farmerId, deliveryState);
////		System.out.println("allCount " + allCount);
////		
////		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
////		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
////		Integer endPage = Math.min(startPage+10-1, allPage);
////		
////		pageInfo.setAllPage(allPage);
////		pageInfo.setStartPage(startPage);
////		pageInfo.setEndPage(endPage);
////		
////		return deliveryList;
////	}
//	
//	// 정산내역
//	public List<InvoiceDto> findInvoicesByFarmerIdAndDateAndPage(Long farmerId, String date, PageInfo pageInfo) throws Exception{
//		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
//		List<Tuple> tuples = null;
//		List<InvoiceDto> invoiceList = new ArrayList<>();
//		Long allCount = null;
//		
////		tuples = farmerDslRepository.findOrdersIdAndDeliveryAndProductAndByDeliveryState(farmerId, , pageRequest);
////		for(Tuple t : tuples) {
////				
////		}
////		allCount = farmerDslRepository.findDeliveryCountByFarmerIdAndDeliveryState(farmerId, deliveryState);
////		
////		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
////		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
////		Integer endPage = Math.min(startPage+10-1, allPage);
////		
////		pageInfo.setAllPage(allPage);
////		pageInfo.setStartPage(startPage);
////		pageInfo.setEndPage(endPage);
////		
//		return invoiceList;
//	}
//	
//	// 파머등록
//	@Override
//	public Farmer registerFarmer(RegFarmerDto request, MultipartFile profileImage) throws Exception {
//		
//		Farmer farmer = Farmer.builder()
//				.farmName(request.getFarmName())
//				.farmTel(request.getFarmTel())
//				.farmAddress(request.getFarmAddress())
//				.farmAddressDetail(request.getFarmAddressDetail())
//				.registrationNum(request.getRegistrationNum())
//				.farmBank(request.getFarmBank())
//				.farmAccountNum(request.getFarmAccountNum())
//				.build();
//		
//		// 관심품목 입력받아서 # 기준으로 파싱하여 각각 저장
//		String[] interests = request.getFarmInterest().replaceAll("^\\s*#*", "").split("#");
//    int numInterests = Math.min(interests.length, 5); // 최대 5개의 관심사로 제한
//
//    farmer.setFarmInterest1(numInterests > 0 ? interests[0].trim() : null);
//    farmer.setFarmInterest2(numInterests > 1 ? interests[1].trim() : null);
//    farmer.setFarmInterest3(numInterests > 2 ? interests[2].trim() : null);
//    farmer.setFarmInterest4(numInterests > 3 ? interests[3].trim() : null);
//    farmer.setFarmInterest5(numInterests > 4 ? interests[4].trim() : null);
//		
//		Farmer savedFarmer = farmerRepository.save(farmer);
//		
//		if (profileImage != null && !profileImage.isEmpty()) {
//			String dir = "C:/Users/USER/upload";
//
//			// 파일명 설정
//			String fileName = "profile_image_" + savedFarmer.getFarmerId() + "."
//					+ StringUtils.getFilenameExtension(profileImage.getOriginalFilename());
//			
//			// 파일 저장 경로 설정
//			String filePath = Paths.get(dir, fileName).toString();
//
//			// 파일 저장
//			profileImage.transferTo(new File(filePath));
//
//			savedFarmer.setFarmPixurl(filePath);
//		}
//		
//		return savedFarmer;
//	}
//
//	// 파머아이디로 파머 가져오기
//	@Override
//	public Farmer getFarmerById(Long farmerId) throws Exception {
//    Optional<Farmer> farmerOptional = farmerRepository.findById(farmerId);
//    return farmerOptional.orElse(null);
//	}
//
//	// 파머 정보 저장
//	@Override
//	@Transactional
//	public Farmer saveFarmer(Farmer farmer) throws Exception {
//		return farmerRepository.save(farmer);
//	}
	
}
