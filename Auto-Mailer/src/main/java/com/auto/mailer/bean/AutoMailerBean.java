package com.auto.mailer.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AutoMailerBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String fromAddress;
	String toAddress;
	String companyName;
	String techSkills;
	String mobileNumber;
	String isStoredInDB;
	String isMailSent;

}
