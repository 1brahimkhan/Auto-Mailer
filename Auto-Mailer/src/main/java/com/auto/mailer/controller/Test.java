package com.auto.mailer.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

	@Value("${auto.mailer.resume.path}")
	private String resumePath;

	@Value("${auto.mailer.ik.mail.id}")
	private String ikMailId;

	@Value("${auto.mailer.kk.mail.id}")
	private String kkMailId;

	@Value("${auto.mailer.ck.mail.id}")
	private String ckMailId;

	@Value("${auto.mailer.max.mail.shot.limit}")
	private Integer maxMailShootLimit;

	@Value("${auto.mailer.linkedin}")
	private String linkedin;

	@Value("${auto.mailer.github}")
	private String github;

	@Value("${auto.mailer.mobile}")
	private String ibMobileNo;

	@GetMapping("/")
	public String test() {
		return "hello";
	}

//	@Scheduled(cron = "0 0/9 * * * ?")
// to run the method post start up every 9 minutes
	@Scheduled(fixedRate = 9 * 60 * 1000) // every 9 minutes
	public void keepAliveMethod() {
		logger.info("This method will run every 9 minutes to avoid in activity while deployment");
	}

	@GetMapping("/paper")
	public String daysLeftForPaperAlert() {
		LocalDate localDate = LocalDate.now();
		LocalDate targetDate = LocalDate.of(2025, 11, 10);

		long daysLeft = ChronoUnit.DAYS.between(localDate, targetDate);

		final String subject = daysLeft + " Days Left For Paper 🔥🔥🔥";
		final String body = "You're not behind. You're just one bold step away from your next big break. Let's go — one application at a time. You got this. 💪🔥";
		emailService.sendMailSimple(ikMailId, ikMailId, subject, body);
		return "Mail Sent, Check your Inbox :)";
	}

	@GetMapping("/testMail")
	public String testMail() {
		Map<String, String> paramsMap = new HashMap<>();

		paramsMap.put("tech_skills", "Java, Spring Boot, Microservices,Rest API, SQL, JavaScript");
		paramsMap.put("company_name", "Google");

		emailService.sendEmail(ikMailId, kkMailId, resumePath, paramsMap);
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

	@GetMapping("/readFromSheets")
	public List<List<Object>> readGSheets() throws IOException, GeneralSecurityException {
		ValueRange response = null;
		Map<String, String> paramsMap = new HashMap<>();
		try {
			Sheets sheetService = gSheetsUtils.getSheetService();
			String spreadSheetId = sheetId;
			String sheetReadRange = Constants.SHEET_RANGE;

			response = sheetService.spreadsheets().values().get(spreadSheetId, sheetReadRange).execute();

			List<List<Object>> rawDataFromSheets = response.getValues();

			List<AutoMailerBean> finalData = readDataAndReturnFinalBeans(rawDataFromSheets);

//				get top 10 records whose is_mail_sent flag is 'N'
			for (int i = 0; i < finalData.size(); i++) {
				String isMailSent = finalData.get(i).getIsMailSent();
				if (Constants.N.equalsIgnoreCase(isMailSent)) {
//				Dynamic params for mail sheets
					paramsMap.put(Constants.TECH_SKILLS, finalData.get(i).getTechSkills());
					paramsMap.put(Constants.COMPANY, finalData.get(i).getCompanyName());
					paramsMap.put(Constants.MOBILE_NUMBER, ibMobileNo);
					paramsMap.put(Constants.LINKEDIN, linkedin);
					paramsMap.put(Constants.GITHUB, github);

//					mail shoot 
					emailService.sendEmail(finalData.get(i).getFromAddress(), finalData.get(i).getToAddress(),
							resumePath, paramsMap);
					logger.info("Mail Sent for to Address :- " + finalData.get(i).getToAddress());
//					update the flag is mail sent in sheets 
					autoMailerService.updateIsMailSentFlagByToAddress(finalData.get(i).getToAddress(), spreadSheetId,
							rawDataFromSheets, i);

				}
			}

			logger.info("All Data From Google Sheets:- " + response.getValues());
			logger.info("Final List Of beans which needs to be inserted in DB :- " + finalData);
		} catch (Exception e) {
			logger.error("Error Occured");
		}

		return response.getValues();

	}

//	connect all features and test all in once
//	R&D about the deployment in window service/aws/railway
//	mail trigger if successfully shoot / failure (phase 2)

	private List<AutoMailerBean> readDataAndReturnFinalBeans(List<List<Object>> rawData) {
		List<AutoMailerBean> listOfBeans = new ArrayList<>();
		List<Object> header = rawData.get(0);
		int count = 0;

		for (int i = 1; i < rawData.size(); i++) {

			List<Object> row = rawData.get(i);
			AutoMailerBean bean = new AutoMailerBean();

			for (int j = 0; j < row.size(); j++) {
				Object headerValue = header.get(j);
				String actualValue = String.valueOf(row.get(j));

				if (actualValue != null && !(actualValue.equalsIgnoreCase(Constants.EMPTY_STRING))
						&& headerValue != null
						&& !(String.valueOf(headerValue).equalsIgnoreCase(Constants.EMPTY_STRING))
						&& headerValue instanceof String) {
					String stringHeaderValue = (String) headerValue;
					switch (stringHeaderValue) {
					case Constants.FROM_ADDRESS:
						bean.setFromAddress(actualValue);
						break;

					case Constants.TO_ADDRESS:
						bean.setToAddress(actualValue);
						break;

					case Constants.COMPANY_NAME:
						bean.setCompanyName(actualValue);
						break;

					case Constants.TECH_SKILLS:
						bean.setTechSkills(actualValue);
						break;

					case Constants.MOBILE_NUMBER:
						bean.setMobileNumber(actualValue);
						break;

					case Constants.IS_MAIL_SENT:
						bean.setIsMailSent(actualValue);
						break;

					default:
						break;
					}
				}
				logger.info("Header or Actual Value is Empty");
			}
			logger.info("Final Bean With Values:- " + bean.toString());
			if (bean.getIsMailSent() != null && bean.getIsMailSent().equalsIgnoreCase("N") && count < 10) {
				listOfBeans.add(bean);
				count++;
			}
		}
		return listOfBeans;
	}

}
