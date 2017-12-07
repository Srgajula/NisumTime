/**
 * 
 */
package com.nisum.mytime.util;

import com.nisum.mytime.model.EmailDomain;

/**
 * @author nisum
 *
 */
public interface MailService {
	
	public String sendEmailWithAttachment(EmailDomain emailObj);
}
