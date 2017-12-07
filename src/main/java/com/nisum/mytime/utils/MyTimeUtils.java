package com.nisum.mytime.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyTimeUtils {

	private MyTimeUtils() {

	}

	public final static String driverUrl = "jdbc:ucanaccess://";
	public final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static DateFormat tdf = new SimpleDateFormat("HH:mm");
	public final static DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
}
