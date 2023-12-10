package com.kosta.farm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoResponseDto {
	private Long userId;
  private Long farmerId;
  private String userName;
  private String userEmail;
  private String userPassword;
  private String userTel;
  private String address1;
  private String address2;
  private String address3;
  private String userRole;

  public UserInfoResponseDto (Long userId, Long farmerId, String userName, String userEmail, String userPassword,
  		String userTel, String address1, String address2, String address3, String userRole) {
      this.userId = userId;
      this.farmerId = farmerId;
      this.userName = userName;
      this.userEmail = userEmail;
      this.userPassword = userPassword;
      this.userTel = userTel;
      this.address1 = address1;
      this.address2 = address2;
      this.address3 = address3;
      this.userRole = userRole;
  }

}
