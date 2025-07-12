package com.auto.mailer.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "emailId")
public class GoogleSheetBean {
	
    private String serialNumber;
    private String emailId;
    private String isMailSent;
    private String date;

}
