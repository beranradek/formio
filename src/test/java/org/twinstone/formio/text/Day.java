package org.twinstone.formio.text;

import java.util.Calendar;
import java.util.Date;

/**
 * Casovy udaj s presnosti na cele dny (datum)
 * 
 * @author Radek Slaby
 */
public class Day extends ImmutableDate {
	private static final long serialVersionUID = 5584585851393881387L;
	
	public Day(int year, int month, int day) {
		super(initTm(year, month, day));
	}
	
	public Day(long tm) {
		super(normalizeTm(tm));
	}
	
	public static Day valueOf(Date date) {
		if (date==null || date instanceof Day)
			return (Day) date;
		return new Day(date.getTime());
	}
		
	protected static long initTm(int year, int month, int day) {
		Calendar c = getCal();
		c.clear();
		c.set(year, month-1, day);
		return c.getTimeInMillis();
	}
	
	protected static long normalizeTm(long tm) {
		Calendar c = getCal();
		c.setTimeInMillis(tm);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	private Object writeReplace() {
		return new DayHandle(getTime());
	}
}
