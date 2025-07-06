package com.auto.mailer.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auto.mailer.bean.AutoMailerBean;
import com.auto.mailer.constants.Constants;
import com.auto.mailer.service.AutoMailerService;
import com.auto.mailer.service.EmailService;
import com.auto.mailer.utils.GSheetServiceUitls;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

@RestController
public class Test {

	@Autowired
	AutoMailerService autoMailerService;

	@Autowired
	EmailService emailService;

	@Autowired
	GSheetServiceUitls gSheetsUtils;

	private static final Logger logger = LogManager.getLogger(Test.class);

	@Value("${auto.mailer.gsheets.id}")
	private String sheetId;

	@GetMapping("/")
	public String test() {
		return "hello";
	}

	@GetMapping("/testMail")
	public String testMail() {
		emailService.sendEmail("test@gmail.com", "test@gmail.com", "You Have Won 1 Crore Lottery ",
				"You Have Won 1 Crore Lottery");
		return "mail send";

	}

	@PostMapping("/start")
	public String doStuff(@RequestBody AutoMailerBean autoMailerBean) {
		autoMailerService.insertDataInDB(autoMailerBean);
		return "done";
	}

//	@Scheduled(cron = "${auto.mailer.cron.time}")
//	public void dummyCronJob() {
//		logger.info("This will run every one minutes");
//	}

//	dummy sheets api call

	@GetMapping("/readFromSheets")
	public List<List<Object>> readGSheets() throws IOException, GeneralSecurityException {
		ValueRange response = null;
		try {
			Sheets sheetService = gSheetsUtils.getSheetService();
			String spreadSheetId = sheetId;
			String sheetReadRange = Constants.SHEET_RANGE;

			response = sheetService.spreadsheets().values().get(spreadSheetId, sheetReadRange).execute();

			logger.info("Data From Google Sheets" + response.getValues());
		} catch (Exception e) {
		}

		return response.getValues();

	}

}
