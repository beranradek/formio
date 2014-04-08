/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 31.7.2007
 *
 * Copyright (c) 2007 Et netera, s.r.o. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package net.formio.format;

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
	
	private static final ThreadLocal<Calendar> THREAD_LOCAL_CALENDAR = new ThreadLocal<Calendar>();
	
	protected static Calendar getCal() {
		Calendar c = THREAD_LOCAL_CALENDAR.get();
		if (c == null) {
			c = Calendar.getInstance();
			THREAD_LOCAL_CALENDAR.set(c);
		}
		return c;
	}
	
	private Object writeReplace() {
		return new ImmutableDateHandle(getTime());
	}

}
