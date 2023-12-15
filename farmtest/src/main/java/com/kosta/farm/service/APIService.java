package com.kosta.farm.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kosta.farm.dto.CompanyDto;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;

@Service
public class APIService {
	
	public List<CompanyDto> requestCompanyList() throws Exception{
		StringBuilder sb = new StringBuilder("http://info.sweettracker.co.kr/api/v1/companylist?");
		String serviceKey = "OstBNzBg0PI7Tr96ol661A";
		
		sb.append(URLDecoder.decode("t_key="+serviceKey, "UTF-8"));
		
		URL url = new URL(sb.toString()); // http://info.sweettracker.co.kr/api/v1/companylist?t_key=
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		
		int code = conn.getResponseCode();
		BufferedReader br;
		
		if (code == 200 ) {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		
		StringBuilder dsb = new StringBuilder();
		String Line = null;
		
		while((Line = br.readLine()) != null) {
			dsb.append(Line);
		}
		br.close();
	    conn.disconnect();
	    
	    List<CompanyDto> comList = new ArrayList<>();
	    JSONParser parser = new JSONParser();
	    JSONObject mobj = (JSONObject)parser.parse(dsb.toString());
	    Long totalCount = (Long)mobj.get("totalCount");
	    JSONArray data = (JSONArray)mobj.get("Company");
	    
	    System.out.println(data.size());
	    for(int i = 0; i < data.size(); i++) {
	    	JSONObject ecJson = (JSONObject)data.get(i);
	    	String international = (String)ecJson.get("International");
	    	String Code = (String)ecJson.get("Code");
	    	String Name = (String)ecJson.get("Name");

	    	if(international.equals("false")) {
	    		comList.add(new CompanyDto(international, Code, Name));		    		
	    	}
	    }
		
		return comList;
	}
}
