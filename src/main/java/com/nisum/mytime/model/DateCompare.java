package com.nisum.mytime.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class DateCompare implements Comparator<EmpLoginData> {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public int compare(EmpLoginData o1, EmpLoginData o2) {

		Date first = null;
		Date second = null;
		try {
			first = formatter.parse(o1.getFirstLogin());
			second = formatter.parse(o2.getFirstLogin());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return first.before(second) ? -1 : first.after(second) ? 1 : 0;
	}
}