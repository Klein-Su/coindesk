package com.cub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cub.entity.CurrencyEntity;
import com.cub.service.ApiCallService;

@RestController
public class ApiCallController {
	private static final Logger log = LoggerFactory.getLogger(ApiCallController.class);
	
    public  static final String COINDESK_API_URL = "https://api.coindesk.com/v1/bpi/currentprice.json";

    private static final String MESSAGE = "message";
    
    @Autowired
    private ApiCallService apiCallService;
    
    @PostMapping(value = "/api/getCoindeskAPI")
    public ResponseEntity<Map<String, Object>> getCoindeskAPI(HttpServletRequest req) throws Exception{
    	
    	log.trace("Call coindesk API and Get JSON data...");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	if(!apiCallService.getCoindeskAPI(COINDESK_API_URL).equals(null)) {
    		response.put(MESSAGE, "呼叫 coindesk API，獲取 JSON 資料成功。");
    		response.put("coindeskAPI", apiCallService.getCoindeskAPI(COINDESK_API_URL).toMap());
    	} else {
    		response.put(MESSAGE, "呼叫 coindesk API，獲取 JSON 資料失敗。");
    	}
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/api/convertCoindeskNewAPI")
    public ResponseEntity<Map<String, Object>> convertCoindeskNewAPI(HttpServletRequest req) throws Exception{
    	
    	log.trace("Convert coindesk API to New API");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	if(!apiCallService.getCoindeskAPI(COINDESK_API_URL).equals(null)) {
    		if(apiCallService.convertCoindeskNewAPI(apiCallService.getCoindeskAPI(COINDESK_API_URL))) {
        		response.put(MESSAGE, "轉換 coindesk API 組成新 API 成功。");
        		response.put("coindeskAPI", apiCallService.getListAll());
        	} else {
        		response.put(MESSAGE, "轉換 coindesk API 組成新 API 失敗。");
        	}
    	}
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/api/getListAll")
    public ResponseEntity<Map<String, Object>> getListAll(HttpServletRequest req) {
    	
    	log.trace("search...");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	List<CurrencyEntity> list = apiCallService.getListAll();
    	
    	response.put("data" , list);
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/api/create")
    public ResponseEntity<Map<String, Object>> create( 
            @RequestParam(value = "cname" 		, required = false) String cname,
            @RequestParam(value = "ename"   	, required = true ) String ename,
            @RequestParam(value = "description" , required = false) String description,
            @RequestParam(value = "eurRate"     , required = true ) String eurRate,
            @RequestParam(value = "gbpRate"     , required = true ) String gbpRate,
            @RequestParam(value = "usdRate"     , required = true ) String usdRate,
            HttpServletRequest request)   {
    	
    	log.trace("create...");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	if(!apiCallService.findByEname(ename).equals(null)) {
    		response.put(MESSAGE, "該筆("+ename+")資料已存在，請勿重新新增。");
    	} else {
    		Long id = apiCallService.create(cname, ename, description, eurRate, gbpRate, usdRate);
        	
        	List<CurrencyEntity> list = apiCallService.getListAll();
        	
        	response.put(MESSAGE, "新增資料成功");
        	response.put("data" , list);
    	}
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/api/update")
    public ResponseEntity<Map<String, Object>> update( 
            @RequestParam(value = "cname" 		, required = false) String cname,
            @RequestParam(value = "ename"   	, required = true ) String ename,
            @RequestParam(value = "description" , required = false) String description,
            @RequestParam(value = "eurRate"     , required = true ) String eurRate,
            @RequestParam(value = "gbpRate"     , required = true ) String gbpRate,
            @RequestParam(value = "usdRate"     , required = true ) String usdRate,
            HttpServletRequest request)   {
    	
    	log.trace("update...");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	if(apiCallService.findByEname(ename) == null) {
    		response.put(MESSAGE, "該筆("+ename+")資料不存在，無法修改。");
    	} else {
    		Long id = apiCallService.update(cname, ename, description, eurRate, gbpRate, usdRate);
        	
        	List<CurrencyEntity> list = apiCallService.getListAll();
        	
        	response.put(MESSAGE, "修改資料成功");
        	response.put("data" , list);
    	}
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PostMapping(value = "/api/delete")
    public ResponseEntity<Map<String, Object>> delete( 
            @RequestParam(value = "ename"   	, required = true ) String ename,
            HttpServletRequest request)   {
    	
    	log.trace("delete...");
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	
    	if(apiCallService.findByEname(ename) == null) {
    		response.put(MESSAGE, "該筆("+ename+")資料不存在, 無法刪除。");
    	} else {
    		Long id = apiCallService.delete(apiCallService.findByEname(ename));
        	
        	List<CurrencyEntity> list = apiCallService.getListAll();
        	
        	response.put(MESSAGE, "刪除("+id+")資料成功");
        	response.put("data" , list);
    	}
    	
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}
