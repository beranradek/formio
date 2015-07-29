/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a specific geographical or cultural region including a time zone offset.
 * 
 * @author Radek Beran
 */
public final class Location {
	private final Locale locale;
	
	private final TimeZone timeZone;
	
	private static final Location DEFAULT = new Location(Locale.getDefault(), TimeZone.getDefault());
	
	static public final Location CZECH = getInstance(new Locale("cs"));
	
	static public final Location ENGLISH = getInstance(Locale.ENGLISH);

    static public final Location FRENCH = getInstance(Locale.FRENCH);

    static public final Location GERMAN = getInstance(Locale.GERMAN);

    static public final Location ITALIAN = getInstance(Locale.ITALIAN);

    static public final Location JAPANESE = getInstance(Locale.JAPANESE);

    static public final Location KOREAN = getInstance(Locale.KOREAN);

    static public final Location CHINESE = getInstance(Locale.CHINESE);

    static public final Location SIMPLIFIED_CHINESE = getInstance(Locale.SIMPLIFIED_CHINESE);
    
    static public final Location SLOVAK = getInstance(new Locale("sk"));

    static public final Location TRADITIONAL_CHINESE = getInstance(Locale.TRADITIONAL_CHINESE);
    
    static public final Location CZECHIA = getInstance(new Locale("cs", "CZ"));

    static public final Location FRANCE = getInstance(Locale.FRANCE);

    static public final Location GERMANY = getInstance(Locale.GERMANY);

    static public final Location ITALY = getInstance(Locale.ITALY);

    static public final Location JAPAN = getInstance(Locale.JAPAN);

    static public final Location KOREA = getInstance(Locale.KOREA);

    static public final Location CHINA = SIMPLIFIED_CHINESE;

    static public final Location PRC = SIMPLIFIED_CHINESE;
    
    static public final Location SLOVAKIA = getInstance(new Locale("sk", "SK"));

    static public final Location TAIWAN = TRADITIONAL_CHINESE;

    static public final Location UK = getInstance(Locale.UK);

    static public final Location US = getInstance(Locale.US);

    static public final Location CANADA = getInstance(Locale.CANADA);

    static public final Location CANADA_FRENCH = getInstance(Locale.CANADA_FRENCH);
	
	public static Location getDefault() {
		return DEFAULT;
	}
	
	public static Location getInstance(Locale locale, TimeZone timeZone) {
		return new Location(locale, timeZone);
	}
	
	public static Location getInstance(Locale locale) {
		return getInstance(locale, TimeZone.getDefault());
	}
	
	public static Location getInstance(TimeZone timeZone) {
		return getInstance(Locale.getDefault(), timeZone);
	}
	
	public static Location getInstance() {
		return getInstance(Locale.getDefault(), TimeZone.getDefault());
	}
	
	private Location(Locale locale, TimeZone timeZone) {
		this.locale = locale;
		this.timeZone = timeZone;
	}

	public Locale getLocale() {
		return locale;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Location))
			return false;
		Location other = (Location) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Location [locale=" + locale + ", timeZone=" + timeZone + "]";
	}
	
}
