package com.auto.mailer.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.auto.mailer.bean.GoogleSheetBean;

@Service
public class GoogleSheetService {
	
	private static final Logger logger = LogManager.getLogger(GoogleSheetService.class);
	
	public Set<GoogleSheetBean> fetchGoogleSheetMailList() {
	
	Set<GoogleSheetBean> uniqueBeans = new LinkedHashSet<>();
	
    try {
        String webAppUrl = "https://script.google.com/macros/s/AKfycbw6jXKSXZeJ6ipGValJ7ctiKOVoxRRFIhjHUWkGFO4lzK6EjGuxh6JRS-mCI6iB8bDx/exec";
        
        String params = "?func=ReadAll&SN=1"; // ReadAll data from Sheet 1
        
        // Create connection
        @SuppressWarnings("deprecation")
		URL url = new URL(webAppUrl + params);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Check response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.trim().split(",").length >= 3) {
            	String[] splitedLine = StringUtils.split(inputLine, ",");
            	GoogleSheetBean googleSheetBean = new GoogleSheetBean();
            	googleSheetBean.setSerialNumber(splitedLine[0]);
            	googleSheetBean.setEmailId(splitedLine[1]);
            	googleSheetBean.setIsMailSent(splitedLine[2]);
            	uniqueBeans.add(googleSheetBean);
                }else {
                	logger.error("Invalid Entry:" +inputLine);
                }
            }
            in.close();
            logger.info("Google Sheet Data "+uniqueBeans);

        } else {
        	logger.error("GET request failed. Response Code: " + responseCode);
        }
    } catch (Exception e) {
    	logger.error("Error Occurred");
    }
    
	return uniqueBeans;
    
	}
}


