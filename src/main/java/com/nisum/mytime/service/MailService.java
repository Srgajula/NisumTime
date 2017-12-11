/**
 * 
 */
package com.nisum.mytime.service;

import com.nisum.mytime.model.EmailDomain;

/**
 * @author nisum
 *
 */
public interface MailService {
	
	public String sendEmailWithAttachment(EmailDomain emailObj);

	public String deletePdfReport(String fileName);
}
