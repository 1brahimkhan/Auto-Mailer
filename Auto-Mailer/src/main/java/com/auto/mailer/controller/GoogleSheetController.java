package com.auto.mailer.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auto.mailer.bean.GoogleSheetBean;
import com.auto.mailer.service.GoogleSheetService;

@RestController
@RequestMapping("/api")
public class GoogleSheetController {
	
	@Autowired
	GoogleSheetService googleSheetService;
	
	@GetMapping("/googleSheetMailList")
	public Set<GoogleSheetBean> getGoogleSheetMailList() {
		return googleSheetService.fetchGoogleSheetMailList();

	}

}