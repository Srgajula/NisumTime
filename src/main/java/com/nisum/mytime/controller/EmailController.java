package com.nisum.mytime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.model.EmailDomain;
import com.nisum.mytime.util.MailService;

@RestController
public class EmailController {
	
	@Autowired
	private MailService mailService;

	@RequestMapping(value="/sendEmail", method=RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> sendAttachmentMail(@RequestBody EmailDomain emailObj){
		String response = mailService.sendEmailWithAttachment(emailObj);
		if("Success".equals(response))
			return new ResponseEntity<>(response,HttpStatus.OK);
		else
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
