/*
 * Created on 25.5.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package org.twinstone.formio.text;

import java.io.Serializable;

/** Serializacni zastupce {@link Day}.
 * 
 * @author Vlastimil Mencik
 */
public class DayHandle implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long time;
	
	public DayHandle(long time) {
		this.time = time;
	}
	
	private Object readResolve() {
		return new Day(time);
	}
}
