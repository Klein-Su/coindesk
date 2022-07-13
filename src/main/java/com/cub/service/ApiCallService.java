package com.cub.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cub.entity.CurrencyEntity;
import com.cub.repo.CurrencyRepo;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class ApiCallService {
	private static final Logger log = LoggerFactory.getLogger(ApiCallService.class);
	
	@Autowired
	private CurrencyRepo currencyRepo;
	
	/**
	 * 獲取 coindesk API 的 JSON 資料
	 * 
	 * @param 	url	API 的 URL
	 * @return 	JSONObject
	 */
	public JSONObject getCoindeskAPI(String url) {
		log.trace("getCoindeskAPI from " + url);
		
		String jsonStr = sendGetRequest(url);
		
		if(jsonStr.equals("false")) {
			log.trace("getCoindeskAPI fail...");
			return null;
		}
		
		if (!jsonStr.isBlank() && Pattern.matches("^[\\[\\{].*[\\]\\}]$", jsonStr)) {
            JSONObject  res = new JSONObject();
            JsonElement json = JsonParser.parseString(jsonStr);
            if (json.isJsonObject()) {
                res.put("data", new JSONObject(jsonStr).toMap());
            } else if (json.isJsonArray()) {
                res.put("data", new JSONArray(jsonStr).toList());
            }
            log.trace("getCoindeskAPI successe...");
            res.put("status", res.has("data"));
            return res;
        } else {
            log.debug(String.format("JSON解析失敗%n%s", jsonStr));
            JSONObject res = new JSONObject();
            res.put("status" , false);
            res.put("message", "JSON解析失敗");
            return res;
        }
	}
	
	public boolean convertCoindeskNewAPI(JSONObject jo) {
		log.trace("convertCoindeskNewAPI from " + jo.toMap());
		
		JSONArray jsonArray = jo.toJSONArray(jo.names());
		
		if (jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				
				if(jsonArray.get(i) instanceof JSONObject) {
					JSONObject job = jsonArray.getJSONObject(i);
	
					String currencyEname = String.valueOf(job.get("chartName"));
					
					CurrencyEntity entity = findByEname(currencyEname);
					
					entity = entity == null ? new CurrencyEntity() : entity;
					
					String timesStr = String.valueOf(job.get("time"));
					Map<String, Object> times = new JSONObject(timesStr).toMap();
					String updatedISOStr = String.valueOf(times.get("updatedISO"));
					LocalDateTime ldt = LocalDateTime.parse(updatedISOStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
					Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
					
					entity.setCreateDate(entity.getCreateDate() == null ? date : entity.getCreateDate());
					entity.setLastModifiedDate(date);
					entity.setEname(currencyEname);
					entity.setDescription(String.valueOf(job.get("disclaimer")));
					
					String bpiStr = String.valueOf(job.get("bpi"));
					JsonElement bpiJson = JsonParser.parseString(bpiStr);
					JSONArray bpiArray = bpiJson.isJsonArray() == true ? (JSONArray) new JSONArray(bpiStr).toList() : null;
					
					if(bpiArray != null && bpiArray.length() > 0) {
						for (int j = 0; j < jsonArray.length(); j++) {
							JSONObject bpiJob = jsonArray.getJSONObject(j);
							
							String eurStr = String.valueOf(bpiJob.get("EUR"));
							Map<String, Object> eurMap = new JSONObject(eurStr).toMap();
							BigDecimal eurRate = new BigDecimal(String.valueOf(eurMap.get("rate_float")));
							
							String gbpStr = String.valueOf(bpiJob.get("GBP"));
							Map<String, Object> gbpMap = new JSONObject(gbpStr).toMap();
							BigDecimal gbpRate = new BigDecimal(String.valueOf(gbpMap.get("rate_float")));
							
							String usdStr = String.valueOf(bpiJob.get("USD"));
							Map<String, Object> usdMap = new JSONObject(usdStr).toMap();
							BigDecimal usdRate = new BigDecimal(String.valueOf(usdMap.get("rate_float")));
							
							if(null != eurRate) {
								entity.setEurRate(eurRate);
							}
							
							if(null != gbpRate) {
								entity.setEurRate(gbpRate);
							}
							
							if(null != usdRate) {
								entity.setEurRate(usdRate);
							}
						}
					}
					
					currencyRepo.save(entity);
				}
		      }
		}
		
		return true;
	}
	
	public List<CurrencyEntity> getListAll() {
		return currencyRepo.findAll();
	}
	
	public long create(String cname, String ename, String description, String eurRate, String gbpRate, String usdRate) {
		CurrencyEntity currencyEntity = new CurrencyEntity();
		
		Date date = new Date();
		
		currencyEntity.setCreateDate(date);
		currencyEntity.setLastModifiedDate(date);
		currencyEntity.setCname(cname);
		currencyEntity.setEname(ename);
		currencyEntity.setDescription(description);
		currencyEntity.setEurRate(new BigDecimal(eurRate));
		currencyEntity.setGbpRate(new BigDecimal(gbpRate));
		currencyEntity.setUsdRate(new BigDecimal(usdRate));
		
		currencyEntity = currencyRepo.save(currencyEntity);
		
		return currencyEntity.getId();
	}
	
	public long update(String cname, String ename, String description, String eurRate, String gbpRate, String usdRate) {
		CurrencyEntity currencyEntity = currencyRepo.findByEname(ename);
		
		Date date = new Date();
		
		currencyEntity.setLastModifiedDate(date);
		currencyEntity.setCname(cname);
		currencyEntity.setEname(ename);
		currencyEntity.setDescription(description);
		currencyEntity.setEurRate(new BigDecimal(eurRate));
		currencyEntity.setGbpRate(new BigDecimal(gbpRate));
		currencyEntity.setUsdRate(new BigDecimal(usdRate));
		
		currencyEntity = currencyRepo.save(currencyEntity);
		
		return currencyEntity.getId();
	}
	
	public long delete(CurrencyEntity entity) {
		Long currencyEntityId = entity.getId();
				
		currencyRepo.delete(entity);
		
		return currencyEntityId;
	}
	
	public CurrencyEntity findByEname(String ename) {
		return currencyRepo.findByEname(ename);
	};
	
	public String sendGetRequest(String url){
    	
    	HttpGet request = new HttpGet(url);
        request.setHeader("Content-Type", "application/json");

        String result = "";
		try {
			CloseableHttpClient   client   = getHttpClient();
	        CloseableHttpResponse response = client.execute(request);
	        int status = response.getStatusLine().getStatusCode();
	        if(status != 200) {
	        	return "false";
	        }
			result = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return result;
    }
    
    public CloseableHttpClient getHttpClient() {
        final int timeout = 30;

        RequestConfig config =
            RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .build();

        return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
}
