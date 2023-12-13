package com.kosta.farm.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.dto.DeliveryDto;
import com.kosta.farm.dto.OrdersDto;
import com.kosta.farm.dto.QuotationDto;
import com.kosta.farm.entity.Delivery;
import com.kosta.farm.dto.RegFarmerDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Orders;
import com.kosta.farm.entity.Quotation;
import com.kosta.farm.entity.Request;
import com.kosta.farm.repository.DeliveryRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.OrdersRepository;
import com.kosta.farm.repository.QuotationRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.unti.PageInfo;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

	// Repository
	private final FarmerRepository farmerRepository;
	private final RequestRepository requestRepository;
	private final DeliveryRepository deliveryRepository;
	private final OrdersRepository ordersRepository;
	private final QuotationRepository quotationRepository;
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
	// 파머페이지 견적현황 state : 0 : 견적서 취소, 1 : 대기중, 2 : 기간 만료, 3 : 결제완료 
	@Override 
	public List<QuotationDto> findQuotationByFarmerIdAndStateAndPage(Long farmerId, String state, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<Tuple> tuples = farmerDslRepository.findQuotationByFarmerIdAndStateAndPaging(farmerId, state, pageRequest);
		List<QuotationDto> resList = new ArrayList<>();
		
		for(Tuple t : tuples) {
			QuotationDto dto = new QuotationDto();
			Quotation quot = t.get(0, Quotation.class);
			String address = t.get(1, String.class);
			
			dto.setQuotationId(quot.getQuotationId());
			dto.setProduct(quot.getQuotationProduct());
			dto.setQuantity(quot.getQuotationQuantity());
			dto.setPrice(quot.getQuotationPrice());
			dto.setAddress(address);
			dto.setState(quot.getQuotationState());
			resList.add(dto);
		}
		
		Long allCount = farmerDslRepository.findQuotationCountByFarmerId(farmerId, state);
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return resList;
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
	public List<OrdersDto> findOrdersByFarmerIdAndPage(Long farmerId, String type, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<Tuple> tuples = null;
		List<OrdersDto> ordList = new ArrayList<>();
		Long allCount = null;
		if(type.equals("1")) { // 매칭 주문
			tuples = farmerDslRepository.findOrdersQuotByFarmerIdAndPaging(farmerId, pageRequest);
			for(Tuple t : tuples) {
				OrdersDto dto = new OrdersDto();
				Long ordersId = t.get(0, Long.class);
				String product = t.get(1, String.class);
				String quantity = t.get(2, String.class);
				Integer price = t.get(3, Integer.class);
				String name = t.get(4, String.class);
				String tel = t.get(5, String.class);
				String address = t.get(6, String.class);
				dto.setOrdersId(ordersId);
				dto.setProduct(product);
				dto.setQuantity(quantity);
				dto.setPrice(price);
				dto.setName(name);
				dto.setTel(tel);
				dto.setAddress(address);
				ordList.add(dto);
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);
		} else if(type.equals("2")) { // 받은 주문
			tuples = farmerDslRepository.findOrdersByFarmerIdAndPaging(farmerId, pageRequest);
			for(Tuple t : tuples) {
				OrdersDto dto = new OrdersDto();
				Long ordersId = t.get(0, Long.class);
				String product = t.get(1, String.class);
				String quantity = t.get(2, String.class);
				Integer price = t.get(3, Integer.class);
				String name = t.get(4, String.class);
				String tel = t.get(5, String.class);
				String address = t.get(6, String.class);
				
				dto.setOrdersId(ordersId);
				dto.setProduct(product);
				dto.setQuantity(quantity);
				dto.setPrice(price);
				dto.setName(name);
				dto.setTel(tel);
				dto.setAddress(address);
				ordList.add(dto);
				
			}
			allCount = farmerDslRepository.findOrdersCountByFarmerIdAndQuotationIsNotNull(farmerId);
		}
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return ordList;
	}
	
	// 결제 완료(매칭) 상세 보기
	public OrdersDto OrdersDetailQuotationId(Long farmerId, Long ordersId, String type) throws Exception{
		Tuple tuple = null;
		OrdersDto orders = new OrdersDto();
		if(type.equals("1")) {
			tuple = farmerDslRepository.findOrderByFarmerIdAndOrderIdIsNotNull(farmerId, ordersId);
		} else if(type.equals("2")) {
			tuple = farmerDslRepository.findOrderByFarmerIdAndOrderIdAndQuotaionIdIsNull(farmerId, ordersId);
		}
		orders.setOrdersId(tuple.get(0, Long.class));
		orders.setProduct(tuple.get(1, String.class));
		orders.setQuantity(tuple.get(2, String.class));
		orders.setName(tuple.get(3, String.class));
		orders.setTel(tuple.get(4, String.class));
		orders.setAddress(tuple.get(5, String.class));
		orders.setPrice(tuple.get(6, Integer.class));
		orders.setPaymentBank(tuple.get(7, String.class));
		orders.setDelivery(tuple.get(8, Integer.class));
		orders.setPaymentState(tuple.get(9, String.class));
		orders.setPaymentPrice(tuple.get(10, Integer.class));
		
		Timestamp timestamp = tuple.get(11, Timestamp.class);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = dateFormat.format(timestamp);        
		orders.setDate(dateString);
		
		return orders;
	}

	// 발송 완료 처리
	@Transactional
	public void insertDelivery(Long ordersId, String tCode, String tInvoice) throws Exception {
		System.out.println("2");
		System.out.println("id " + ordersId);
		System.err.println("tCode " + tCode);
		System.out.println("tInvoice " + tInvoice);
		Delivery delivery = new Delivery(ordersId, tCode, tInvoice);
		deliveryRepository.save(delivery);
//		farmerDslRepository.insertDeliveryWithOrdersIdAndTCodeAndTInvoice(ordersId, tCode, tInvoice);
	}
	
	// 판매 취소
	@Transactional
	public void deleteOrderState(Long farmerId, Long ordersId, String cancelText) throws Exception {
		// Orders State 변경
		farmerDslRepository.deleteOrderState(ordersId, farmerId, cancelText);
		
		// Payment State 변경
		Orders ord = ordersRepository.findById(ordersId).get();
		Long paymentId = ord.getPaymentId();
		farmerDslRepository.updatePaymentByOrdersId(paymentId);
	}

	// 배송 현황 리스트
	@Override
	public List<DeliveryDto> findDeliberyByFarmerIdAndDeliveryState(Long farmerId, String deliveryState,
			PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 10); // 첫번째 값 : 페이지 번호, 두 번째 값 : 페이지 크기
		List<Tuple> tuples = null;
		List<DeliveryDto> deliveryList = new ArrayList<>();
		Long allCount = null;
			tuples = farmerDslRepository.findOrdersIdAndDeliveryAndProductAndByDeliveryState(farmerId, deliveryState, pageRequest);
			for(Tuple t : tuples) {
				DeliveryDto dto = new DeliveryDto();
				Long ordersId = t.get(0, Long.class);
				String tCode = t.get(1, String.class);
				String tInvoice = t.get(2, String.class);
				String product = t.get(3, String.class);
				String state = t.get(4, String.class);
				
				dto.setOrdersId(ordersId);
				dto.setTCode(tCode);
				dto.setTInvoice(tInvoice);
				dto.setProduct(product);
				dto.setDeliveryState(deliveryState);
				deliveryList.add(dto);
			}	
		
		allCount = farmerDslRepository.findDeliveryCountByFarmerIdAndDeliveryState(farmerId, deliveryState);
		
		Integer allPage = (int)(Math.ceil(allCount.doubleValue()/pageRequest.getPageSize()));
		Integer startPage = (pageInfo.getCurPage()-1)/10*10+1;
		Integer endPage = Math.min(startPage+10-1, allPage);
		
		pageInfo.setAllPage(allPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return deliveryList;
	}
	
	// 파머등록
	@Override
	public Farmer registerFarmer(RegFarmerDto request, MultipartFile profileImage) throws Exception {
		
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
		String[] interests = request.getFarmInterests().replaceAll("^\\s*#*", "").split("#");
    int numInterests = Math.min(interests.length, 5); // 최대 5개의 관심사로 제한

    farmer.setFarmInterest1(numInterests > 0 ? interests[0].trim() : null);
    farmer.setFarmInterest2(numInterests > 1 ? interests[1].trim() : null);
    farmer.setFarmInterest3(numInterests > 2 ? interests[2].trim() : null);
    farmer.setFarmInterest4(numInterests > 3 ? interests[3].trim() : null);
    farmer.setFarmInterest5(numInterests > 4 ? interests[4].trim() : null);
		
		Farmer savedFarmer = farmerRepository.save(farmer);
		
		if (profileImage != null && !profileImage.isEmpty()) {
			String dir = "C:\\Users\\USER\\upload";

			// 파일명 설정
			String fileName = "profile_image_" + savedFarmer.getFarmerId() + "."
					+ StringUtils.getFilenameExtension(profileImage.getOriginalFilename());

			// 파일 저장 경로 설정
			String filePath = Paths.get(dir, fileName).toString();

			// 파일 저장
			profileImage.transferTo(new File(filePath));

			savedFarmer.setFarmPixurl(filePath);
		}
		
		return savedFarmer;
	}
	
}
