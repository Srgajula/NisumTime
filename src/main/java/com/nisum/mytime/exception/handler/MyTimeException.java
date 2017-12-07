package com.nisum.mytime.exception.handler;

/**
 * The Class BusinessException.
 *
 * @author r.dnyandeo.narkhede
 */
public class MyTimeException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3489301441198508177L;

	public MyTimeException() {
	}

	public MyTimeException(String arg0) {
		super(arg0);
	}

	public MyTimeException(Throwable arg0) {
		super(arg0);
	}

	public MyTimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MyTimeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
