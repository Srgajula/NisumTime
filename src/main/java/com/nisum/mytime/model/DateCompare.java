package com.nisum.mytime.model;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import com.nisum.mytime.utils.MyTimeLogger;
import com.nisum.mytime.utils.MyTimeUtils;

public class DateCompare implements Comparator<EmpLoginData> {

	public int compare(EmpLoginData o1, EmpLoginData o2) {

		Date first = null;
		Date second = null;
		try {
			first = MyTimeUtils.df.parse(o1.getFirstLogin());
			second = MyTimeUtils.df.parse(o2.getFirstLogin());
			int result = (first.after(second) ? 1 : 0);
			return (first.before(second) ? -1 : result);
		} catch (ParseException e) {
			MyTimeLogger.getInstance().info(e.getMessage());
		}
		return -1;
	}
}