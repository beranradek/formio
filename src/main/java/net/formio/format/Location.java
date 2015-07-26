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
	
	static public final Location CZECH = new Location(new Locale("cs"));
	
	static public final Location ENGLISH = new Location(Locale.ENGLISH);

    static public final Location FRENCH = new Location(Locale.FRENCH);

    static public final Location GERMAN = new Location(Locale.GERMAN);

    static public final Location ITALIAN = new Location(Locale.ITALIAN);

    static public final Location JAPANESE = new Location(Locale.JAPANESE);

    static public final Location KOREAN = new Location(Locale.KOREAN);

    static public final Location CHINESE = new Location(Locale.CHINESE);

    static public final Location SIMPLIFIED_CHINESE = new Location(Locale.SIMPLIFIED_CHINESE);
    
    static public final Location SLOVAK = new Location(new Locale("sk"));

    static public final Location TRADITIONAL_CHINESE = new Location(Locale.TRADITIONAL_CHINESE);
    
    static public final Location CZECHIA = new Location(new Locale("cs", "CZ"));

    static public final Location FRANCE = new Location(Locale.FRANCE);

    static public final Location GERMANY = new Location(Locale.GERMANY);

    static public final Location ITALY = new Location(Locale.ITALY);

    static public final Location JAPAN = new Location(Locale.JAPAN);

    static public final Location KOREA = new Location(Locale.KOREA);

    static public final Location CHINA = SIMPLIFIED_CHINESE;

    static public final Location PRC = SIMPLIFIED_CHINESE;
    
    static public final Location SLOVAKIA = new Location(new Locale("sk", "SK"));

    static public final Location TAIWAN = TRADITIONAL_CHINESE;

    static public final Location UK = new Location(Locale.UK);

    static public final Location US = new Location(Locale.US);

    static public final Location CANADA = new Location(Locale.CANADA);

    static public final Location CANADA_FRENCH = new Location(Locale.CANADA_FRENCH);
	
	public static Location getDefault() {
		return DEFAULT;
	}
	
	public Location(Locale locale, TimeZone timeZone) {
		this.locale = locale;
		this.timeZone = timeZone;
	}
	
	public Location(Locale locale) {
		this(locale, DEFAULT.getTimeZone());
	}
	
	public Location(TimeZone timeZone) {
		this(DEFAULT.getLocale(), timeZone);
	}
	
	public Location() {
		this(DEFAULT.getLocale());
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
	
	
}
