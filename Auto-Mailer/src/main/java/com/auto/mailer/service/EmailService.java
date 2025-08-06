package com.auto.mailer.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.auto.mailer.constants.Constants;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private static final Logger logger = LogManager.getLogger(EmailService.class);

	@Autowired
	JavaMailSender javaMailSender;

	@Value("${auto.mailer.email.template.path}")
	String templatePath;

	@Value("${auto.mailer.email.subject}")
	String emailSubject;

	public void sendEmail(String fromAddress, String toAddress, String attachmentPath, Map<String, String> paramsMap) {
		try {

			String finalMailBody = getEmailBody(templatePath, paramsMap);

			String finalSubject = emailSubject.replace(Constants.PARAM_COMPANY_NAME,
					paramsMap.get(Constants.COMPANY_NAME));

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setFrom(fromAddress);
			helper.setTo(toAddress);
			helper.setSubject(finalSubject);
			helper.setText(finalMailBody);

//			for attachments (resume)
//			For Dynamic
			Resource file = new ClassPathResource(attachmentPath);

//			For Local
//			FileSystemResource file = new FileSystemResource(new File(attachmentPath));

			if (file.exists()) {
				helper.addAttachment(file.getFilename(), file);
			} else {
				logger.info("Attachment Not Found");
			}

			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Error Occured", e);
		}

	}

	private String getEmailBody(String mailTemplatePath, Map<String, String> paramsMap) {
		String mailBody = null;
		try {
			Resource file = new ClassPathResource(mailTemplatePath);
			try (InputStream inputStream = file.getInputStream()) {
				mailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}

			for (Map.Entry<String, String> entrySet : paramsMap.entrySet()) {
				mailBody = mailBody.replace("{{" + entrySet.getKey() + "}}", entrySet.getValue());
			}

		} catch (IOException e) {
			logger.error("Error Occurred", e);
		}
		return mailBody;
	}

	public void sendMailSimple(String fromAddress, String toAddress, String subject, String body) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(fromAddress);
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(body);
			helper.setCc("chetan.kandarkar99@gmail.com");
		} catch (MessagingException e) {
			logger.error("Error Occured", e);
		}

		javaMailSender.send(mimeMessage);
	}

}
