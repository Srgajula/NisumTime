package com.nisum.mytime.utils;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MyTimeLogger {

	/** The logging instance. */
	private static MyTimeLogger loggingInstance = null;

	/** The logger. */
	private Logger logger = null;

	private MyTimeLogger() {
		logger = LogManager.getLogger("com.nisum.mytime.utils.MyTimeLogger");
	}

	public synchronized static MyTimeLogger getInstance() {
		if (loggingInstance == null) {
			loggingInstance = new MyTimeLogger();
			return loggingInstance;
		} else {
			return loggingInstance;
		}
	}

	private String getClassName() {
		String className = "";
		try {
			className = Thread.currentThread().getStackTrace()[3].getClassName();
		} catch (Exception e) {
			logger.error("Exception occured due to: ", e);
		}
		return className;
	}

	private String getMethodName() {
		String methodName = "";
		try {
			methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		} catch (Exception e) {
			logger.error("Exception occured due to: ", e);
		}
		return methodName;
	}

	public void entry() {
		String message = "Entry";
		String logMsg = "TID :"  + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.INFO, logMsg);
	}

	public void info(String message) {
		String logMsg = "TID: " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.INFO, logMsg);

	}

	public void warn(String message) {
		String logMsg = " TID : " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.WARN, logMsg);
	}

	public void warn(String message, Throwable t) {
		String logMsg = getClassName() + " : " + getMethodName() + " - " + message;

		logger.log(Level.WARN, logMsg, t);
	}

	public void debug(String message) {
		String logMsg = " TID: " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.DEBUG, logMsg);
	}

	public void debug(String message, Throwable t) {
		String logMsg = " TID:" + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.DEBUG, logMsg, t);
	}

	public void error(String message) {
		String logMsg = "TID :  " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.ERROR, logMsg);
	}

	public void error(String message, Throwable t) {
		String logMsg = getClassName() + " : " + getMethodName() + " - " + message;
		logger.log(Level.ERROR, logMsg, t);
	}

	public void fatal(String message) {
		String logMsg = "TID: " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.FATAL, logMsg);
	}

	public void trace(String message) {
		String logMsg = "TID : " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.TRACE, logMsg);
	}

	public void log(String msg) {
		logger.log(Level.INFO, msg);
	}

	public void exit() {
		String message = "Exit";
		String logMsg = "TID : " + Thread.currentThread().getId() + " : " + getClassName() + " : " + getMethodName()
				+ " - " + message;
		logger.log(Level.INFO, logMsg);
	}
}
