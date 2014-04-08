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
package net.formio.format;

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
