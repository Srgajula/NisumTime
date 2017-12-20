/**
 * 
 */
package com.nisum.mytime.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nisum.mytime.model.EmailDomain;

/**
 * @author nisum
 *
 */
@Service
public class MailServiceImpl implements MailService {

	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	JavaMailSender emailSender;

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Override
	public String sendEmailWithAttachment(EmailDomain emailObj) {
		String response = "Success";
		MimeMessage msg = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(emailObj.getToEmail());
			helper.setCc(emailObj.getCcEmail());
			helper.setFrom(fromEmail);
			helper.setSubject(emailObj.getEmpId() + " - Login hours Report");
			helper.setText("Hi,\n PFA for your login hours report for the period: " + emailObj.getFromDate() + " / "
					+ emailObj.getToDate());
			String fileName = emailObj.getEmpId() + "_" + emailObj.getFromDate() + "_" + emailObj.getToDate()+".pdf";
			File file = resourceLoader.getResource("/WEB-INF/reports/" + fileName).getFile();
			FileSystemResource fileSystem = new FileSystemResource(file);
			helper.addAttachment(fileSystem.getFilename(), fileSystem);
			helper.setSentDate(new Date());
			emailSender.send(msg);
		} catch (MessagingException e) {
			response = "Mail sending failed due to " + e.getMessage();
			logger.error("Mail sending failed due to: ", e);
		} catch (IOException e) {
			response = "Mail sending failed due to " + e.getMessage();
			logger.error("Mail sending failed due to: ", e);
		}
		return response;
	}

	@Override
	public String deletePdfReport(String fileName) {
		String response = "";
		try {
			File file = resourceLoader.getResource("/WEB-INF/reports/" + fileName+".pdf").getFile();
			if(file.exists()){
				boolean status = file.delete();
				if(status){
					response = "Success";
				}
			}
		} catch (IOException e) {
			response = "Report deletion failed due to: " + e.getMessage();
			logger.error("Report deletion failed due to: ", e);
		}
		return response;
	}
}
