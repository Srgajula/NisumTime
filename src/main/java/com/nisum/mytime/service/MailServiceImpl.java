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

	@Override
	public String sendEmailWithAttachment(EmailDomain emailObj) {
		String response = "Success";
		MimeMessage msg = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(emailObj.getToEmail());
			helper.setCc(emailObj.getCcEmail());
			String fromDate = emailObj.getFromDate();
			String toDate = emailObj.getToDate();
			String subject = "";
			String mailContent = "";
			String empId = emailObj.getEmpId();
			if("".equals(empId)){
				empId = "0";
				subject = "All employees - Login hours Report for the period: "+fromDate+" to "+toDate;
				mailContent = "Hi,\n PFA for All employees login hours report for the period: " + fromDate + " to "+ toDate;
			}else{
				subject = empId+ " - Login hours Report for the period: "+fromDate+" to "+toDate;
				mailContent = "Hi,\n PFA for Employee ID: "+empId+" login hours report for the period: " + fromDate + " to "+ toDate;
			}
			helper.setSubject(subject);
			helper.setText(mailContent);
			String fileName = empId + "_" + fromDate + "_" + toDate+".pdf";
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
			if(null != file && file.exists()){
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
