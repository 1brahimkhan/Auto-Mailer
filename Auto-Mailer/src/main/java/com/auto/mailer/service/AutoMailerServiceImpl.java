package com.auto.mailer.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auto.mailer.bean.AutoMailerBean;
import com.auto.mailer.constants.Constants;
import com.auto.mailer.utils.GSheetServiceUitls;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class AutoMailerServiceImpl implements AutoMailerService {

//	@Autowired
//	AutoMailerDao autoMailerDao;

	@Autowired
	GSheetServiceUitls gSheetsUtils;

	private static final Logger logger = LogManager.getLogger(AutoMailerServiceImpl.class);

	@Override
	public void insertDataInDB(AutoMailerBean autoMailerBean) {
		try {
//			autoMailerDao.save(autoMailerBean);
		} catch (Exception e) {
			logger.error("Error Occurred");
		}
	}

	@Override
	public boolean existsBytoAddress(String toAddress) {
//		return autoMailerDao.existsBytoAddress(toAddress);
		return false;

	}

	@Override
	public int updateDbFlag(String toAddress) {
//		return autoMailerDao.updateDbFlag(toAddress, Constants.Y);
		return 1;
	}

	@Override
	public void updateIsMailSentFlagByToAddress(String toAddress, String spreadSheetId,
			List<List<Object>> rawDataFromSheets, int index) {
		List<Object> row = rawDataFromSheets.get(index + 1);
		if (row.size() > 1 && (toAddress.equalsIgnoreCase(row.get(1).toString()))) {
			String isMailSentRange = String.format("Sheet1!" + Constants.CELL_ALPHABET_OF_IS_MAIL_SENT + "%d",
					index + 2);
			ValueRange body = new ValueRange().setValues(List.of(List.of(Constants.Y)));
			try {
				gSheetsUtils.getSheetService().spreadsheets().values().update(spreadSheetId, isMailSentRange, body)
						.setValueInputOption("RAW").execute();
			} catch (IOException | GeneralSecurityException e) {
				logger.error("Error Occured", e);
			}

		}
	}

}
