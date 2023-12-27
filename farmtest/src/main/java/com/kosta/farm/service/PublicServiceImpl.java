package com.kosta.farm.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.kosta.farm.dto.CompanyDto;
import com.kosta.farm.dto.FarmerInfoDto;
import com.kosta.farm.dto.RequestDto;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Request;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.RequestRepository;
import com.kosta.farm.util.PageInfo;
import com.kosta.farm.util.RequestStatus;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
	private final FarmDslRepository farmDslRepository;
	private final RequestRepository requestRepository;
	private final FarmerRepository farmerRepository;

	@Value("${upload.path}")
	private String uploadPath;

	@Value("${swetter.apiKey}")
	private String serviceKey;
	
	@Override
	public void readImage(Integer num, OutputStream out) throws Exception {
		System.out.println(uploadPath);
		System.out.println("hehrerererehrjkweharkaesr = " + uploadPath + num);
		FileInputStream fis = new FileInputStream(uploadPath + num);
		FileCopyUtils.copy(fis, out);
		fis.close();
	}

	// 택배 API
	@Override
	public List<CompanyDto> requestCompanyList() throws Exception {
		StringBuilder sb = new StringBuilder("http://info.sweettracker.co.kr/api/v1/companylist?");

		sb.append(URLDecoder.decode("t_key=" + serviceKey, "UTF-8"));

		URL url = new URL(sb.toString()); // http://info.sweettracker.co.kr/api/v1/companylist?t_key=
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		int code = conn.getResponseCode();
		BufferedReader br;

		if (code == 200) {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder dsb = new StringBuilder();
		String Line = null;

		while ((Line = br.readLine()) != null) {
			dsb.append(Line);
		}
		br.close();
		conn.disconnect();

		List<CompanyDto> comList = new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONObject mobj = (JSONObject) parser.parse(dsb.toString());
		Long totalCount = (Long) mobj.get("totalCount");
		JSONArray data = (JSONArray) mobj.get("Company");

		System.out.println(data.size());
		for (int i = 0; i < data.size(); i++) {
			JSONObject ecJson = (JSONObject) data.get(i);
			String international = (String) ecJson.get("International");
			String Code = (String) ecJson.get("Code");
			String Name = (String) ecJson.get("Name");

	    	if(international.equals("false")) {
	    		comList.add(new CompanyDto(international, Code, Name));		    		
	    	}
	    }
//		System.out.println("here " + comList.size());
		return comList;
	}

	@Override
	@Transactional
	public List<RequestDto> requestListByPage(PageInfo pageInfo) throws Exception {
		return farmDslRepository.requestListWithNameByPage(pageInfo);
	}

	@Override
	public Long requestCountByState(RequestStatus state) throws Exception {
		List<Request> requests = requestRepository.findByState(state);
		return (long) requests.size();
	}

	@Override
	public Long countFarmersWithRating() throws Exception {
		return farmerRepository.countByRatingIsNotNull();
	}

	public Double avgTotalRating() throws Exception {

		Long numberOfFarmersWithRating = farmerRepository.countByRatingIsNotNull();
		System.out.println(numberOfFarmersWithRating);
		if (numberOfFarmersWithRating > 0) {
			List<Farmer> farmersWithRating = farmerRepository.findAllByRatingIsNotNull(); // rating이 null이 아닌 농부 목록을 가져옴
			double totalRating = 0;
			for (Farmer farmer : farmersWithRating) {
				totalRating += farmer.getRating();
			}
			return totalRating / numberOfFarmersWithRating;

		} else {
			return 0.0; // 평점이 있는 농부가 없는 경우
		}
	}

	@Override // 파머리스트 sorting으로
	public List<FarmerInfoDto> findFarmersWithSorting(String sortType, PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() - 1, 15,
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

}
