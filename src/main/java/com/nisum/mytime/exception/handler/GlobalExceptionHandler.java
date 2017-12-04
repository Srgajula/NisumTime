package com.nisum.mytime.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessExceptions(DataAccessException ex){
		log.error("DataAccessException occured due to: ",ex);
		return "DataAccessException occured due to: "+ex.toString();
		
	}
	
	@ExceptionHandler(Exception.class)
	public String handleOtherExceptions(Exception ex){
		log.error("Exception occured due to: ",ex);
		return "Exception occured due to: "+ex.toString();
	}
}