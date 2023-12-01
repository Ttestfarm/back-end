package com.kosta.farm.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.farm.entity.Farmer;
import com.kosta.farm.entity.Farmerfollow;
import com.kosta.farm.entity.Product;
import com.kosta.farm.entity.Productfile;
import com.kosta.farm.entity.Review;
import com.kosta.farm.repository.FarmDslRepository;
import com.kosta.farm.repository.FarmerDslRepository;
import com.kosta.farm.repository.FarmerRepository;
import com.kosta.farm.repository.FarmerfollowRepository;
import com.kosta.farm.repository.ProductFileRepository;
import com.kosta.farm.repository.ProductRepository;
import com.kosta.farm.repository.ReviewRepository;
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

	@Override
	public void regFarmer(Farmer farmer) throws Exception {
		farmerRepository.save(farmer);

	}

	@Override
	public List<Farmer> farmerListByPage(PageInfo pageInfo) throws Exception {
		PageRequest pageRequest =PageRequest.of(pageInfo.getCurPage()-1, 8,Sort.by(Sort.Direction.DESC, "num"));

		return null;
	}
	@Override
	public List<Farmer> searchListByPage(String category, PageInfo pageInfo) throws Exception {
		return null;
	}

	@Override
	public Long farmerCount() throws Exception {
		return farmDslRepository.findFarmerCount();
	}

	@Override
	public Boolean selectFarmerfollow(Integer userId, Integer farmerId) throws Exception {
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
	@Override
	public Boolean selectedFarmerfollow(Integer userId, Integer farmerId) throws Exception {
		Long cnt = farmDslRepository.findIsFarmerfollow(userId, farmerId);
		if (cnt < 1)
			return false;
		return true;
	}

	@Override
	public Farmer farmerInfo(Integer farmerId) throws Exception {
		return null;
	}

	@Override
	public List<Farmerfollow> getFollowingFarmersByUserId(Integer userId) throws Exception {
		return farmerfollowRepository.findByUserId(userId);
	}

	@Override
	public Integer productEnter(Product product, MultipartFile thumbNail, List<MultipartFile> files) throws Exception {
		String dir = "c:/jisu/upload/";
		String fileNums = "";
		if (thumbNail != null && !thumbNail.isEmpty()) {
			Productfile imageFile = Productfile.builder().directory(dir).fileName(thumbNail.getOriginalFilename())
					.size(thumbNail.getSize()).build();
			productFileRepository.save(imageFile);
			File uploadFile = new File(dir + imageFile.getFileId());
			thumbNail.transferTo(uploadFile);
			product.setThumbNail(imageFile.getFileId());
		}

		if (files != null && files.size() != 0) {

			for (MultipartFile file : files) {
				// primgfiletable에 insert
				Productfile imageFile = Productfile.builder().directory(dir).fileName(file.getOriginalFilename())
						.size(file.getSize()).build();
				productFileRepository.save(imageFile);

				// upload 폴더에 upload
				File uploadFile = new File(dir + imageFile.getFileId());
				file.transferTo(uploadFile);

				// file 번호 목록 만들기
				if (!fileNums.equals(""))
					fileNums += ",";
				fileNums += imageFile.getFileId();
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

	@Override
	public List<Review> getReviewListByFarmer(Integer farmerId) throws Exception {
		return farmDslRepository.findByFarmerId(farmerId);
	}

	@Override
	public void insertReview(Review review) throws Exception {
		reviewRepository.save(review);
	}

	@Override
	public List<Review> getReviewListByUser(Integer userId) throws Exception {
		return farmDslRepository.findByUserId(userId);
	}


}
