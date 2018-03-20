/**
 * 
 */
package com.nisum.mytime.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import com.nisum.mytime.model.EmployeeVisa;
import com.nisum.mytime.model.TravelRequest;
import com.nisum.mytime.model.Visa;
import com.nisum.mytime.repository.EmployeeRolesRepo;
import com.nisum.mytime.repository.EmployeeVisaRepo;
import com.nisum.mytime.repository.TravelRepo;
import com.nisum.mytime.repository.VisaRepo;

/**
 * @author nisum
 *
 */
@Service
public class VisaServiceImpl implements VisaService {

	private static final Logger logger = LoggerFactory.getLogger(VisaServiceImpl.class);

	@Autowired
	private VisaRepo visaRepo;
	@Autowired
	private TravelRepo travelRepo;
	
	@Autowired
	private EmployeeVisaRepo employeeVisaRepo;

	@Override
	public List<Visa> getAllVisas() {
		return visaRepo.findAll();
	}

	@Override
	public List<EmployeeVisa> getAllEmployeeVisas() {
		return employeeVisaRepo.findAll();
	}

	@Override
	public EmployeeVisa addEmployeeVisas(EmployeeVisa e) {
		// TODO Auto-generated method stub
		return employeeVisaRepo.save(e);
	}

	@Override
	public EmployeeVisa updateEmployeeVisas(EmployeeVisa e) {
		// TODO Auto-generated method stub
		return employeeVisaRepo.save(e);
	}

	@Override
	public void deleteEmployeeVisas(EmployeeVisa e) {
		// TODO Auto-generated method stub
		 employeeVisaRepo.delete(e);
	}
	@Override
	public List<TravelRequest> getAllTravels() {
		return travelRepo.findAll();
	}
	@Override
	public TravelRequest addTravelRequest(TravelRequest e) {
		// TODO Auto-generated method stub
		return travelRepo.save(e);
	}

	@Override
	public TravelRequest updateTravelRequest(TravelRequest e) {
		// TODO Auto-generated method stub
		return travelRepo.save(e);
	}

	@Override
	public void deleteEmployeeVisas(TravelRequest e) {
		// TODO Auto-generated method stub
		travelRepo.delete(e);
	}
}
