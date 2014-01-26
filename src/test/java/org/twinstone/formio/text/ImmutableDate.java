/*
 * Created on 31.7.2007
 *
 * Copyright (c) 2007 Et netera, s.r.o. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package org.twinstone.formio.text;

import java.util.Calendar;
import java.util.Date;

/**
 * Immutable variant of {@link java.util.Date} class.
 * Any attempt to change its value will result in an exception.
 * <p>
 * This class used to be part of jNP project but has been
 * moved here in July 2009.
 *
 * <ul>
 * <li>Thread-safe: Yes, of course. It is immutable. ;)
 * </ul>
 *
 * @author Martin Kacer
 */
public class ImmutableDate extends Date
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see Date#Date()
	 */
	public ImmutableDate() {
		super();
	}

	/**
	 * @see Date#Date(long)
	 */
	public ImmutableDate(long date) {
		super(date);
	}

	/**
	 * Generate an exception.
	 * @throws UnsupportedOperationException this exception is always generated.
	 */
	private void attemptToModify() {
		throw new UnsupportedOperationException("this Date instance is immutable");
	}

	//inherit javadoc: @see java.util.Date#setDate(int)
	@Override @Deprecated
	public void setDate(int date) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setHours(int)
	@Override @Deprecated
	public void setHours(int hours) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setMinutes(int)
	@Override @Deprecated
	public void setMinutes(int minutes) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setMonth(int)
	@Override @Deprecated
	public void setMonth(int month) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setSeconds(int)
	@Override @Deprecated
	public void setSeconds(int seconds) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setTime(long)
	@Override
	public void setTime(long time) {
		attemptToModify();
	}

	//inherit javadoc: @see java.util.Date#setYear(int)
	@Override @Deprecated
	public void setYear(int year) {
		attemptToModify();
	}
	
	private static final ThreadLocal<Calendar> tlCal = new ThreadLocal<Calendar>();
	
	protected static Calendar getCal() {
		Calendar c = tlCal.get();
		if (c == null) {
			c = Calendar.getInstance();
			tlCal.set(c);
		}
		return c;
	}
	
	private Object writeReplace() {
		return new ImmutableDateHandle(getTime());
	}

}
