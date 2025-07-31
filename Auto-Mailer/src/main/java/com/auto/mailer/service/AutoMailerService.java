package com.auto.mailer.service;

import java.util.List;

import com.auto.mailer.bean.AutoMailerBean;

public interface AutoMailerService {

	void insertDataInDB(AutoMailerBean autoMailerBean);

	boolean existsBytoAddress(String toAddress);

	int updateDbFlag(String toAddress);

	void updateIsMailSentFlagByToAddress(String toAddress, String spreadSheetId, List<List<Object>> rawDataFromSheets,
			int i);

}
